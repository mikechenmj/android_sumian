package com.sumian.devicedemo.sleepdata.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.devicedemo.R
import com.sumian.devicedemo.sleepdata.util.TimeUtil
import com.sumian.devicedemo.sleepdata.util.TimeUtilV2
import com.sumian.devicedemo.sleepdata.widget.calendarView.CalendarView
import com.sumian.devicedemo.sleepdata.widget.custom.CalendarPopup
import kotlinx.android.synthetic.main.view_weekly_sleep_data_date_bar.view.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/16 19:50
 * desc   :
 * version: 1.0
 */
class WeeklySleepDataDateBar(context: Context, attributeSet: AttributeSet) :
        FrameLayout(context, attributeSet) {
    private var mCalendarPopup: CalendarPopup? = null
    private var mDataLoader: CalendarPopup.DataLoader? = null
    private var mOnDateClickListener: CalendarView.OnDateClickListener? = null
    private var mSelectedTime = 0L
    private var mPreviewDays = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_weekly_sleep_data_date_bar, this, true)
        iv_pre.setOnClickListener { onDateClick(mSelectedTime - DateUtils.WEEK_IN_MILLIS) }
        iv_next.setOnClickListener { onDateClick(mSelectedTime + DateUtils.WEEK_IN_MILLIS) }
        tv_time.setOnClickListener { showCalendarPop() }
        updateCurrentTime(TimeUtilV2.getWeekStartDayTime(System.currentTimeMillis()))
    }

    fun setPreviewDays(previewDays: Int) {
        mPreviewDays = previewDays
    }

    private fun showCalendarPop() {
        mCalendarPopup = CalendarPopup(context, mDataLoader!!, mPreviewDays)
        mCalendarPopup!!.setOnDateClickListener(CalendarView.OnDateClickListener { time ->
            onDateClick(
                    time
            )
        })
        mCalendarPopup!!.setSelectDayTime(mSelectedTime)
        mCalendarPopup!!.setWeekMode(true)
        mCalendarPopup!!.showAsDropDown(
                vg_calendar_bar,
                0,
                resources.getDimension(R.dimen.space_10).toInt()
        )

    }

    private fun onDateClick(time: Long) {
        val weekStartTime = TimeUtilV2.getWeekStartDayTime(time)
        updateCurrentTime(weekStartTime)
        mOnDateClickListener?.onDateClick(weekStartTime)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentTime(time: Long) {
        mSelectedTime = time
        val dayStartTime = TimeUtil.getDayStartTime(System.currentTimeMillis())
        val weekStartDayTime = TimeUtil.getWeekStartDayTime(time)
        val weekEndDayTime = TimeUtil.getWeekEndDayTime(time)
        tv_time.text = "${formatDate(weekStartDayTime)}-${formatDate(weekEndDayTime)}"
        iv_next.visibility = if (dayStartTime < weekEndDayTime) View.GONE else View.VISIBLE
    }

    private fun formatDate(time: Long): CharSequence? {
        return DateFormat.format("MM.dd", Date(time))
    }

    fun setDataLoader(dataLoader: CalendarPopup.DataLoader) {
        mDataLoader = dataLoader
    }

    fun addMonthAndData(startMonthTime: Long, hasDataDays: Set<Long>, isInit: Boolean) {
        mCalendarPopup?.addMonthAndData(startMonthTime, hasDataDays, isInit)
    }

    fun setOnDateClickListener(listener: CalendarView.OnDateClickListener) {
        mOnDateClickListener = listener
    }

    fun setCurrentTime(time: Long) {
        updateCurrentTime(TimeUtilV2.getWeekStartDayTime(time))
    }

    fun getCurrentTime(): Long {
        return mSelectedTime
    }
}
