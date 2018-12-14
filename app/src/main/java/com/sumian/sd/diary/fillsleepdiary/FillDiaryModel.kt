package com.sumian.sd.diary.fillsleepdiary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.bean.SleepPill

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 10:42
 * desc   :
 * version: 1.0
 */
class FillDiaryModel : ViewModel() {
    var mFinishedProgress = 0  // 0-8
    var mCurrentProgress = 1
    val mSleepTimeLiveData = MutableLiveData<SleepTimeData>()   //t0 - t3
    val mNightWakeLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mDaySleepLiveData = MutableLiveData<Pair<Int, Int>>()    // x time, y minus
    val mFeelingLiveData = MutableLiveData<Int>()    // 0-4
    val mPillsLiveData = MutableLiveData<List<SleepPill>>()
    val mMarkLiveData = MutableLiveData<String>()
    var mSwitchProgressListener: SwitchProgressListener? = null

    init {
        mCurrentProgress = 1
        mSleepTimeLiveData.value = SleepTimeData()
    }

    fun previous() {
        mCurrentProgress = Math.max(1, mCurrentProgress - 1)
        mSwitchProgressListener?.switchProgress(mCurrentProgress, false)
    }

    fun next() {
        if (mFinishedProgress <= mCurrentProgress) {
            ToastUtils.showShort(R.string.please_complete_the_question)
            return
        }
        mCurrentProgress = Math.min(FillDiaryConst.TOTAL_PAGE, mCurrentProgress + 1)
        mSwitchProgressListener?.switchProgress(mCurrentProgress, true)
    }

    fun growFinishedProgress(progress: Int) {
        if (mFinishedProgress < progress) {
            mFinishedProgress = progress
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

    fun getSleepTime(index: Int): Long {
        return mSleepTimeLiveData.value!!.getTime(index)
    }
}