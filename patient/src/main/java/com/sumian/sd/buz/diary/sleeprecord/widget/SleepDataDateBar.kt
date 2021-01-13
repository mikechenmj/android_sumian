package com.sumian.sd.buz.diary.sleeprecord.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.buz.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.view_sleep_data_date_bar.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/16 16:02
 * desc   :
 * version: 1.0
 */
class SleepDataDateBar(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private var mCalendarPopup: CalendarPopup? = null
    private var mDataLoader: CalendarPopup.DataLoader? = null
    private var mOnDateClickListener: CalendarView.OnDateClickListener? = null
    private var mSelectedTime = 0L
    private var mPreviewDays = 0
    private var mOnSleepDataDateBarClickListener: OnSleepDataDateBarClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sleep_data_date_bar, this, true)
//        val onDateClickListener: (View) -> Unit = { showDatePopup(show = !iv_date_arrow.isActivated) }
//        iv_date_arrow.setOnClickListener(onDateClickListener)
//        tv_date.setOnClickListener(onDateClickListener)
        setCurrentTime(TimeUtilV2.getDayStartTime(System.currentTimeMillis()))
        iv_date_left.setOnClickListener { dateLeftClickListener() }
        iv_date_right.setOnClickListener { dateRightClickListener() }
    }

    private lateinit var dateLeftClickListener: () -> Unit
    fun setOnDateLeftListener(f: () -> Unit) {
        dateLeftClickListener = f
    }

    private lateinit var dateRightClickListener: () -> Unit
    fun setOnDateRightListener(f: () -> Unit) {
        dateRightClickListener = f
    }

    fun setOnSleepDataDateBarClickListener(onSleepDataDateBarClickListener: OnSleepDataDateBarClickListener) {
        mOnSleepDataDateBarClickListener = onSleepDataDateBarClickListener
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

    fun setWeekIconClickListener(onClickListener: OnClickListener) {
        iv_weekly_report.setOnClickListener(onClickListener)
    }

    fun setPreviewDays(previewDays: Int) {
        mPreviewDays = previewDays
    }

    fun showDatePopup(show: Boolean = !iv_date_arrow.isActivated) {
        mOnSleepDataDateBarClickListener?.onClick(show)
        iv_date_arrow.isActivated = show
        if (show) {
            mCalendarPopup = CalendarPopup(context, mDataLoader!!, mPreviewDays)
            mCalendarPopup!!.setOnDateClickListener(CalendarView.OnDateClickListener { time ->
                mOnDateClickListener?.onDateClick(time)
                setCurrentTime(time)
            })
            mCalendarPopup!!.setOnDismissListener { iv_date_arrow.isActivated = false }
            mCalendarPopup!!.setSelectDayTime(mSelectedTime)
            mCalendarPopup!!.showAsDropDown(this, 0, 0)
        }
    }

    fun setCurrentTime(time: Long) {
        mSelectedTime = time
        tv_date.text = TimeUtil.formatDate("yyyy/MM/dd", time)
    }

    fun getCurrentTime(): Long {
        return mSelectedTime
    }

    interface OnSleepDataDateBarClickListener {
        fun onClick(show: Boolean)
    }

}