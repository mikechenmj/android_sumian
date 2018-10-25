package com.sumian.sd.diary.sleeprecord.calendar.custom

import android.content.Context
import android.view.ViewGroup
import android.widget.PopupWindow
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.utils.TimeUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 14:32
 * desc   :
 * version: 1.0
 */
class CalendarPopup(context: Context, dataLoader: DataLoader)
    : PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
    companion object {
        private const val PRELOAD_MONTH_COUNT = 12
    }

    private val mCurrentTimeInMillis = System.currentTimeMillis()
    private var mOnDateClickListener: CalendarView.OnDateClickListener? = null
    private val mDataLoader: DataLoader

    private val mCalendarViewWrapper: SleepCalendarViewWrapper by lazy {
        val calendarViewWrapper = SleepCalendarViewWrapper(context)
        calendarViewWrapper.apply {
            setTodayTime(mCurrentTimeInMillis)
            setSelectDayTime(mCurrentTimeInMillis)
            setOnBgClickListener { dismiss() }
            setLoadMoreListener { dataLoader.loadData(it, PRELOAD_MONTH_COUNT, false) }
            setOnDateClickListener {
                if (it > TimeUtil.getStartTimeOfTheDay(System.currentTimeMillis())) {
                    return@setOnDateClickListener
                }
                dismiss()
                mOnDateClickListener?.onDateClick(it)
            }
        }
    }

    init {
        contentView = mCalendarViewWrapper
        isOutsideTouchable = true
        this.setBackgroundDrawable(null)
        animationStyle = 0
        isFocusable = true
        mDataLoader = dataLoader
        mDataLoader.loadData(mCurrentTimeInMillis, PRELOAD_MONTH_COUNT, true)
    }

    fun setOnDateClickListener(listener: CalendarView.OnDateClickListener) {
        mOnDateClickListener = listener
    }

    fun setSelectDayTime(selectedTime: Long) {
        mCalendarViewWrapper.setSelectDayTime(selectedTime)
    }

    fun addMonthAndData(startMonthTime: Long, hasDataDays: Set<Long>, isInit: Boolean) {
        mCalendarViewWrapper.addHasDataDays(hasDataDays)
        mCalendarViewWrapper.addMonthTimes(TimeUtil.createMonthTimes(startMonthTime, PRELOAD_MONTH_COUNT, isInit), isInit)
    }

    interface DataLoader {
        fun loadData(startMonthTime: Long, monthCount: Int, isInit: Boolean)
    }

}