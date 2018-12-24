package com.sumian.sd.diary.fillsleepdiary.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import kotlinx.android.synthetic.main.layout_choose_sleep_time.*
import java.lang.IllegalArgumentException

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 15:56
 * desc   : 参考需求文档 1.14.0 睡眠日记改版
 * version: 1.0
 */
class ChooseSleepTimeFragment : BaseFillSleepDiaryFragment() {
    private var mType = TYPE_SLEEP_TIME
    private var mTimeIndex = 0

    companion object {
        private const val KEY_TYPE = "ChooseSleepTimeFragment.KEY_TYPE"
        const val TYPE_SLEEP_TIME = 0
        const val TYPE_FALL_ASLEEP_TIME = 1
        const val TYPE_WAKEUP_TIME = 2
        const val TYPE_GET_UP_TIME = 3

        fun newInstance(progress: Int, type: Int): ChooseSleepTimeFragment {
            val fragment = ChooseSleepTimeFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, progress)
            bundle.putInt(KEY_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentViewLayout(): Int {
        return R.layout.layout_choose_sleep_time
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mType = bundle.getInt(KEY_TYPE)
        mTimeIndex = getTimeIndexByType()
    }

    override fun initWidget() {
        super.initWidget()
        initPicker()
    }

    override fun initData() {
        super.initData()
        mFillDiaryViewModel.mSleepTimeLiveData.observe(this, Observer {
            fill_sleep_diagram_view.setTimeAndCurrentIndex(it.mTimeArray, mTimeIndex)
        })
    }

    private fun initPicker() {
        val hour = getCurrentHour()
        val minute = getCurrentMinute()
        val hours = getHours()
        val minutes = getMinutes(hour)
        picker_hour.refreshByNewDisplayedValues(hours)
        picker_hour.value = getIndexOfArray(hour.toString(), hours)
        picker_minute.refreshByNewDisplayedValues(minutes)
        picker_minute.value = getIndexOfArray(minute.toString(), minutes)
        picker_hour.setOnValueChangedListener { picker, oldVal, newVal ->
            run {
                val newHour = getHours()[newVal]!!.toInt()
                setTime(newHour, getCurrentMinute())
                picker_minute.refreshByNewDisplayedValues(getMinutes(newHour))
            }
        }
        picker_minute.setOnValueChangedListener { picker, oldVal, newVal ->
            run { setTime(getCurrentHour(), getMinutes(getCurrentHour())[newVal]!!.toInt()) }
        }
    }

    private fun getHours(): Array<String?> {
        return mFillDiaryViewModel.mSleepTimeLiveData.value!!.createHoursByIndex(mTimeIndex)
    }

    private fun getMinutes(hour: Int): Array<String?> {
        return mFillDiaryViewModel.mSleepTimeLiveData.value!!.createMinutesByIndexAndHour(mTimeIndex, hour)
    }

    private fun setTime(hour: Int, minute: Int) {
        mFillDiaryViewModel.setSleepTime(mTimeIndex, hour, minute)
    }

    private fun getCurrentTime(): Long {
        return mFillDiaryViewModel.getSleepTime(mTimeIndex)
    }

    private fun getCurrentHour(): Int {
        return TimeUtilV2.getHourOfDay(getCurrentTime())
    }

    private fun getCurrentMinute(): Int {
        return TimeUtilV2.getMinute(getCurrentTime())
    }

    private fun getTimeIndexByType(): Int {
        return when (mType) {
            TYPE_SLEEP_TIME -> 0
            TYPE_FALL_ASLEEP_TIME -> 1
            TYPE_WAKEUP_TIME -> 2
            TYPE_GET_UP_TIME -> 3
            else -> throw IllegalArgumentException("illegal type")
        }
    }

    private fun getIndexOfArray(value: String, array: Array<String?>): Int {
        for ((index, v) in array.withIndex()) {
            if (value == v) {
                return index
            }
        }
        return 0
    }
}