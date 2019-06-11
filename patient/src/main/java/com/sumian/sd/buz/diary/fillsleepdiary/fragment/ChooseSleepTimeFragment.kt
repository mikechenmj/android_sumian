package com.sumian.sd.buz.diary.fillsleepdiary.fragment

import android.os.Bundle
import android.text.format.DateUtils
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.utils.SumianExecutor
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.sd.R
import com.sumian.sd.buz.diary.fillsleepdiary.bean.SleepTimeData
import kotlinx.android.synthetic.main.layout_choose_sleep_time.*
import kotlinx.android.synthetic.main.view_fill_diary_container.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 15:56
 * desc   : 参考需求文档 1.14.0 睡眠日记改版
 * version: 1.0
 */
class ChooseSleepTimeFragment : BaseFillSleepDiaryFragment() {
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

    private var mType = TYPE_SLEEP_TIME
    private var mTimeIndex = 0

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
        tv_no_sleep.isVisible = mType == TYPE_FALL_ASLEEP_TIME
        tv_no_sleep.setOnClickListener {
            val times = mFillDiaryViewModel.mSleepTimeLiveData.value!!.mTimeArray
            mFillDiaryViewModel.setSleepTime(TYPE_FALL_ASLEEP_TIME, times[0])
            mFillDiaryViewModel.setSleepTime(TYPE_WAKEUP_TIME, times[0])
            mFillDiaryViewModel.noSleep()
        }
    }

    override fun initData() {
        super.initData()
        mFillDiaryViewModel.mSleepTimeLiveData.observe(this, Observer {
            fill_sleep_diagram_view.mNoSleep = mFillDiaryViewModel.mNoSleep
            fill_sleep_diagram_view.setTimeAndCurrentIndex(it.mTimeArray, mTimeIndex)
            updateTodayYesterdayUI(it.mTimeArray[mTimeIndex] >= SleepTimeData.TODAY_00_00)
        })
    }

    private fun initPicker() {
        val hour = getCurrentHour()
        val minute = getCurrentMinute()
        val hours = getHours()
        val minutes = getMinutes(hour, getCurrentTime() >= SleepTimeData.TODAY_00_00)
        picker_hour.refreshByNewDisplayedValues(hours)
        picker_hour.value = getCurrentHourIndex(SleepTimeData.formatNumber(hour), hours)
        picker_minute.refreshByNewDisplayedValues(minutes)
        picker_minute.value = getIndexOfArray(SleepTimeData.formatNumber(minute), minutes)
        picker_hour.setOnValueChangedListener(mOnHourChangeListener)
        picker_hour.setOnScrollListener(mOnHourScrollListener)
        picker_minute.setOnValueChangedListener(mOnMinuteChangeListener)
    }

    override fun onDestroyView() {
        picker_hour.setOnValueChangedListener(null)
        picker_hour.setOnScrollListener(null)
        picker_minute.setOnValueChangedListener(null)
        super.onDestroyView()
    }

    private val mOnHourChangeListener = NumberPickerView.OnValueChangeListener { picker, oldVal, newVal ->
        val newHour = getHours()[newVal]!!.toInt()
        val isToday = isTodayByHourIndex(newVal)
        val minutes = getMinutes(newHour, isToday)
        picker_minute.refreshByNewDisplayedValues(minutes)
        setTime(newHour, minutes[picker_minute.value]!!.toInt(), isToday)
    }

    private val mOnHourScrollListener = NumberPickerView.OnScrollListener { view, scrollState ->
        LogUtils.d(view.value, getHours()[view.value])
        SumianExecutor.runOnUiThread(Runnable { updateTodayYesterdayUI(isTodayByHourIndex(view.value)) })
    }

    private val mOnMinuteChangeListener = NumberPickerView.OnValueChangeListener { picker, oldVal, newVal ->
        setTime(
                getCurrentHour(),
                getMinutes(getCurrentHour(), getCurrentTime() >= SleepTimeData.TODAY_00_00)[newVal]!!.toInt(),
                getCurrentTime() >= SleepTimeData.TODAY_00_00)
    }

    private fun getHours(): Array<String?> {
        return mFillDiaryViewModel.mSleepTimeLiveData.value!!.createHoursByIndex(mTimeIndex)
    }

    private fun getMinutes(hour: Int, isToday: Boolean): Array<String?> {
        return mFillDiaryViewModel.mSleepTimeLiveData.value!!.createMinutesByIndexAndHour(mTimeIndex, hour, isToday)
    }

    private fun setTime(hour: Int, minute: Int, needAddDay: Boolean) {
        val time = parseHHmmToTime(hour, minute) + if (needAddDay) DateUtils.DAY_IN_MILLIS else 0
        mFillDiaryViewModel.setSleepTime(mTimeIndex, time)
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
        val list = getIndexListOfArray(value, array)
        return if (list.isEmpty()) 0 else list[0]
    }

    private fun getIndexListOfArray(value: String, array: Array<String?>): List<Int> {
        val list = ArrayList<Int>()
        for ((index, v) in array.withIndex()) {
            if (value == v) {
                list.add(index)
            }
        }
        return list
    }

    private fun getCurrentHourIndex(value: String, array: Array<String?>): Int {
        val currentTime = getCurrentTime()
        val list = getIndexListOfArray(value, array)
        return if (list.isEmpty()) {
            0
        } else if (list.size == 1 || currentTime < SleepTimeData.TODAY_00_00) {
            list[0]
        } else {
            list[1]
        }
    }

    private fun updateTodayYesterdayUI(isToday: Boolean) {
        today_yesterday_view.setIsToday(isToday)
    }

    private fun isTodayByHourIndex(hourIndex: Int): Boolean {
        return when (mType) {
            // 准备睡觉时间为A，范围：18:00～17:55
            TYPE_SLEEP_TIME -> hourIndex >= getHours().indexOf("00")
            // 由于睡着时间为B，范围：A～23:50（今天），如果hourIndex与23:50的距离 <= 00:00（24），则说明B是今天
            TYPE_FALL_ASLEEP_TIME -> getHours().size - hourIndex <= 24
            // 醒来时间为C，范围：(B,23:55] ∩ [00:00,23:55]
            // 起床时间为D，范围：[C,23:55]（今天）
            TYPE_WAKEUP_TIME, TYPE_GET_UP_TIME -> true
            else -> throw IllegalArgumentException("illegal type")
        }
    }

    private fun parseHHmmToTime(hour: Int, minute: Int, addDay: Boolean = false): Long {
        return TimeUtilV2.parseTimeStr("HH:mm", "$hour:$minute") + DateUtils.DAY_IN_MILLIS * (if (addDay) 1 else 0)
    }
}