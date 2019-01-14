package com.sumian.sd.diary.fillsleepdiary

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.fillsleepdiary.bean.SleepDiaryData
import com.sumian.sd.diary.fillsleepdiary.bean.SleepTimeData
import com.sumian.sd.diary.sleeprecord.bean.SleepPill
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.network.callback.BaseSdResponseCallback
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 10:42
 * desc   :
 * version: 1.0
 */
class FillDiaryViewModel : ViewModel() {
    private var mCurrentProgress = 0
    val mSleepTimeLiveData = MutableLiveData<SleepTimeData>()   //t0 - t3
    val mNightWakeLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mDaySleepLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mFeelingLiveData = MutableLiveData<Int>()    // 0-4
    val mPillsLiveData = MutableLiveData<List<SleepPill>>()
    val mRemarkLiveData = MutableLiveData<String>()
    var mProgressListener: ProgressListener? = null
    var mDayTime = System.currentTimeMillis()

    private val mCalls = ArrayList<Call<*>>()

    companion object {
        const val TOTAL_PAGE = 9
    }

    init {
        mCurrentProgress = 0
        mSleepTimeLiveData.value = SleepTimeData()
        mNightWakeLiveData.value = null
        mDaySleepLiveData.value = null
        mFeelingLiveData.value = null
        getPreviousSleepPills()
    }

    fun previous() {
        mProgressListener?.onProgressChange(--mCurrentProgress, false)
    }

    fun next() {
        if (!isNextPageAvailable()) {
            ToastUtils.showShort(R.string.please_complete_the_question)
            return
        }
        if (mCurrentProgress == TOTAL_PAGE - 1) {
            postDiaryToServer()
            return
        }
        mCurrentProgress++
        // update next sleep time
        when (mCurrentProgress) {
            in 1..3 -> mSleepTimeLiveData.value!!.updateTimeInNeed(mCurrentProgress)
            else -> Unit
        }
        mProgressListener?.onProgressChange(mCurrentProgress, true)
    }

    private fun postDiaryToServer() {
        val yesterdayStartTime = TimeUtilV2.getDayStartTime(System.currentTimeMillis()) - DateUtils.DAY_IN_MILLIS
        val sleepDiaryData = SleepDiaryData(
                (mDayTime / 1000).toInt(),
                yesterdayStartTime + getSleepTime(0),
                yesterdayStartTime + getSleepTime(1),
                yesterdayStartTime + getSleepTime(2),
                yesterdayStartTime + getSleepTime(3),
                mNightWakeLiveData.value!!.first,
                mNightWakeLiveData.value!!.second,
                mDaySleepLiveData.value!!.second,
                mDaySleepLiveData.value!!.second,
                mFeelingLiveData.value!!,
                mPillsLiveData.value,
                mRemarkLiveData.value)
        val call = AppManager.getSdHttpService().postSleepDiary(sleepDiaryData)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<SleepRecord>() {
            override fun onSuccess(response: SleepRecord?) {
                mProgressListener?.finishWithResult(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    /**
     * 0-3 sleep time
     * 4 night wake
     * 5 day sleep
     * 6 pills
     * 7 feelings
     * 8 mark
     */
    private fun isNextPageAvailable(): Boolean {
        return when (mCurrentProgress) {
            in 0..3 -> true
            4 -> mNightWakeLiveData.value != null
            5 -> mDaySleepLiveData.value != null
            6 -> true
            7 -> mFeelingLiveData.value != null
            8 -> true
            else -> false
        }
    }

    interface ProgressListener {
        fun onProgressChange(index: Int, next: Boolean)
        fun finishWithResult(sleepRecord: SleepRecord?)
    }

    fun setSleepTime(index: Int, time: Long) {
        val data = mSleepTimeLiveData.value
        data?.setTime(index, time)
        mSleepTimeLiveData.value = data
    }

    fun setSleepTime(index: Int, hour: Int, minute: Int) {
        val data = mSleepTimeLiveData.value
        data?.setTime(index, hour, minute)
        mSleepTimeLiveData.value = data
    }

    fun getSleepTime(index: Int): Long {
        return mSleepTimeLiveData.value!!.getTime(index)
    }

    fun getSleepDuration(): Long {
        return getSleepTime(2) - getSleepTime(1)
    }

    private fun getPreviousSleepPills() {
        val call = AppManager.getSdHttpService().getSleepPills()
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<List<SleepPill>>() {
            override fun onSuccess(response: List<SleepPill>?) {
                mPillsLiveData.value = response
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        for (call in mCalls) {
            call.cancel()
        }
    }

}