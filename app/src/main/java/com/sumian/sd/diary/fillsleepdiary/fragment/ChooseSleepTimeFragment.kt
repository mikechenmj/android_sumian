package com.sumian.sd.diary.fillsleepdiary.fragment

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.utils.SumianExecutor
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.diary.fillsleepdiary.bean.SleepTimeData
import kotlinx.android.synthetic.main.layout_choose_sleep_time.*
import kotlinx.android.synthetic.main.tx_video_palyer_controller.*
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
            updateTodayYesterdayUI(it.mTimeArray[mTimeIndex] >= SleepTimeData.TODAY_00_00)
        })
    }

    private fun initPicker() {
        updatePickerValue()
        picker_hour.setOnValueChangedListener { picker, oldVal, newVal ->
            run {
                val newHour = getHours()[newVal]!!.toInt()
                val isToday = isTodayByHourIndex(newVal)
                val minutes = getMinutes(newHour, isToday)
                picker_minute.refreshByNewDisplayedValues(minutes)
                setTime(newHour, minutes[picker_minute.value]!!.toInt(), isToday)
            }
        }
        picker_hour.setOnScrollListener { view, scrollState ->
            LogUtils.d(view.value, getHours()[view.value])
            SumianExecutor.runOnUiThread(Runnable { updateTodayYesterdayUI(isTodayByHourIndex(view.value)) })
        }
        picker_minute.setOnValueChangedListener { picker, oldVal, newVal ->
            run {
                setTime(
                        getCurrentHour(),
                        getMinutes(getCurrentHour(), getCurrentTime() >= SleepTimeData.TODAY_00_00)[newVal]!!.toInt(),
                        getCurrentTime() >= SleepTimeData.TODAY_00_00)
            }
        }
    }

    private fun updatePickerValue() {
        val hour = getCurrentHour()
        val minute = getCurrentMinute()
        val hours = getHours()
        val minutes = getMinutes(hour, getCurrentTime() >= SleepTimeData.TODAY_00_00)
        picker_hour.refreshByNewDisplayedValues(hours)
        picker_hour.value = getIndexOfArray(hour.toString(), hours)
        picker_minute.refreshByNewDisplayedValues(minutes)
        picker_minute.value = getIndexOfArray(minute.toString(), minutes)
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
        for ((index, v) in array.withIndex()) {
            if (value == v) {
                return index
            }
        }
        return 0
    }

    private fun updateTodayYesterdayUI(isToday: Boolean) {
        tv_top.visibility = if (isToday) View.VISIBLE else View.GONE
        tv_bottom.visibility = if (isToday) View.GONE else View.VISIBLE
        tv_middle.text = getString(if (isToday) R.string.today else R.string.yesterday_night)
    }

    private fun isTodayByHourIndex(hourIndex: Int): Boolean {
        return when (mType) {
            // 准备睡觉时间为A，范围：18:00～17:55
            TYPE_SLEEP_TIME -> hourIndex >= getHours().indexOf("0")
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