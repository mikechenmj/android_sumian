package com.sumian.sddoctor.widget.calendar.calendarview.base

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 20:35
 *     desc   :
 *     version: 1.0
 * </pre>
 */
interface DayTypeProvider {
    fun getDayTypeByTime(timeInMillis: Long): Int
}