package com.sumian.sd.diary.fillsleepdiary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.R
import com.sumian.sd.diary.fillsleepdiary.bean.SleepTimeData
import com.sumian.sd.diary.sleeprecord.bean.SleepPill

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 10:42
 * desc   :
 * version: 1.0
 */
class FillDiaryViewModel : ViewModel() {
    var mCurrentProgress = 0
    val mSleepTimeLiveData = MutableLiveData<SleepTimeData>()   //t0 - t3
    val mNightWakeLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mDaySleepLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mFeelingLiveData = MutableLiveData<Int>()    // 0-4
    val mPillsLiveData = MutableLiveData<List<SleepPill>>()
    val mMarkLiveData = MutableLiveData<String>()
    var mSwitchProgressListener: SwitchProgressListener? = null

    companion object {
        const val TOTAL_PAGE = 9
    }

    init {
        mCurrentProgress = 0
        mSleepTimeLiveData.value = SleepTimeData()
        mNightWakeLiveData.value = null
        mDaySleepLiveData.value = null
        mFeelingLiveData.value = null
    }

    fun previous() {
        mSwitchProgressListener?.switchProgress(--mCurrentProgress, false)
    }

    fun next() {
        if (!isNextPageAvailable()) {
            ToastUtils.showShort(R.string.please_complete_the_question)
            return
        }
        mCurrentProgress = Math.min(TOTAL_PAGE, mCurrentProgress + 1)
        mSwitchProgressListener?.switchProgress(mCurrentProgress, true)
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
            8 -> false
            else -> false
        }
    }

    interface SwitchProgressListener {
        fun switchProgress(index: Int, next: Boolean)
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
}