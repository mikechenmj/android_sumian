package com.sumian.sddoctor.widget.calendar.calendarview.base

import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 20:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class AbsCalendarViewAdapter : RecyclerView.Adapter<CalendarViewVH>() {
    abstract fun setTime(time: Long)
    abstract fun setOnDateClickListener(onDateClickListener: AbsCalendarView.OnDateClickListener)
    abstract fun setDayTypeProvider(dayTypeProvider: DayTypeProvider)
}