package com.sumian.sddoctor.widget.calendar.calendarview.week

import android.content.Context
import android.util.AttributeSet
import com.sumian.sddoctor.widget.calendar.calendarview.base.AbsCalendarView
import com.sumian.sddoctor.widget.calendar.calendarview.base.AbsCalendarViewAdapter

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 20:03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class WeeklyCalendarView(context: Context, attrs: AttributeSet?) : AbsCalendarView(context, attrs) {

    constructor(context: Context) : this(context, null)

    override fun createCalendarViewAdapter(): AbsCalendarViewAdapter {
        return WeekCalendarViewAdapter()
    }
}