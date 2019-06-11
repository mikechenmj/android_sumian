package com.sumian.sd.buz.diary.fillsleepdiary

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.diary.fillsleepdiary.bean.SleepDiaryData
import com.sumian.sd.buz.diary.fillsleepdiary.bean.SleepMedicine
import com.sumian.sd.buz.diary.fillsleepdiary.bean.SleepTimeData
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepPill
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.common.network.callback.BaseSdResponseCallback


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 10:42
 * desc   :
 * version: 1.0
 */
class FillDiaryViewModel : BaseViewModel() {
    private var mCurrentProgress = 0
    val mSleepTimeLiveData = MutableLiveData<SleepTimeData>()   //t0 - t3
    val mNightWakeLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mDaySleepLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mFeelingLiveData = MutableLiveData<Int>()    // 0-4
    val mPillsLiveData = MutableLiveData<List<SleepPill>>()
    var mHasHistoryPills = false
    val mRemarkLiveData = MutableLiveData<String>()
    var mProgressListener: ProgressListener? = null
    var mDayTime = System.currentTimeMillis()
    var mMedicines = ArrayList<String>()
    var mNoSleep = false

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
        getSleepMedicine()
    }

    private fun getSleepMedicine() {
        AppManager.getSdHttpService().getMedicines().enqueue(object : BaseSdResponseCallback<PaginationResponseV2<SleepMedicine>>() {

            override fun onSuccess(response: PaginationResponseV2<SleepMedicine>?) {
                if (response == null) {
                    return
                }
                val list = response.data.sortedWith(compareBy(SleepMedicine::weight, SleepMedicine::weight)).reversed().filter { it.enable != 0 }
                for (m in list) {
                    mMedicines.add(m.name)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }

        })
    }

    fun previous() {
        if (mNoSleep && (mCurrentProgress == 3 || mCurrentProgress == 5)) {
            mCurrentProgress -= 2
            mProgressListener?.onProgressChange(mCurrentProgress, false)
        } else {
            mProgressListener?.onProgressChange(--mCurrentProgress, false)
        }
    }

    fun next() {
        if (!isNextPageAvailable()) {
            ToastUtils.showShort(com.sumian.sd.R.string.please_complete_the_question)
            return
        }
        if (mCurrentProgress == 4) {
            if (mNightWakeLiveData.value!!.second * DateUtils.MINUTE_IN_MILLIS > getSleepDuration()) {
                ToastUtils.showShort(com.sumian.sd.R.string.night_wake_time_cant_bigger_than_sleep_time)
                return
            }
        }
        if (mCurrentProgress == TOTAL_PAGE - 1) {
            postDiaryToServer()
            return
        }
        mCurrentProgress += if (mNoSleep && mCurrentProgress == 3) 2 else 1
        // update next sleep time
        when (mCurrentProgress) {
            in 1..3 -> mSleepTimeLiveData.value!!.updateTimeInNeed(mCurrentProgress)
            else -> Unit
        }
        mProgressListener?.onProgressChange(mCurrentProgress, true)
        if (mCurrentProgress == 2) {
            mNoSleep = false
        }
    }

    /**
     * 整夜未睡，跳过睡着时间和醒来时间（两者上报的时候按起床时间计算），
     */
    fun noSleep() {
        mNoSleep = true
        mCurrentProgress = 3
        mSleepTimeLiveData.value?.refreshGetUpTimeWhenNoSleep()
        mProgressListener?.onProgressChange(mCurrentProgress, true)
    }

    private fun postDiaryToServer() {
        val sleepDiaryData = SleepDiaryData(
                (mDayTime / 1000).toInt(),
                getRealSleepTimeInSecond(0),
                getRealSleepTimeInSecond(if (mNoSleep) 3 else 1),
                getRealSleepTimeInSecond(if (mNoSleep) 3 else 2),
                getRealSleepTimeInSecond(3),
                if (mNoSleep) 0 else mNightWakeLiveData.value!!.first,
                if (mNoSleep) 0 else mNightWakeLiveData.value!!.second,
                mDaySleepLiveData.value!!.first,
                mDaySleepLiveData.value!!.second,
                mFeelingLiveData.value!!,
                mPillsLiveData.value,
                mRemarkLiveData.value)
        val call = AppManager.getSdHttpService().postSleepDiary(sleepDiaryData)
        addCall(call)
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
     * 数组中存的 sleep time 是从 1970年1月1日 开始的，通过该方法转换成睡眠的那天如 2019年1月15日
     */
    private fun getRealSleepTimeInSecond(index: Int): Int {
        val yesterdayStartTime = TimeUtilV2.getDayStartTime(mDayTime) - DateUtils.DAY_IN_MILLIS
        val sleepTime = getSleepTime(index)
        return ((yesterdayStartTime + sleepTime - TimeUtilV2.getStartTimeOfTheDay(0)) / 1000).toInt()
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
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<List<SleepPill>>() {
            override fun onSuccess(response: List<SleepPill>?) {
                mHasHistoryPills = response != null && !response.isEmpty()
                mPillsLiveData.value = response
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    fun getMedicines(): Array<String?> {
        var array = arrayOfNulls<String>(mMedicines.size)
        array = mMedicines.toArray(array)
        return array
    }
}