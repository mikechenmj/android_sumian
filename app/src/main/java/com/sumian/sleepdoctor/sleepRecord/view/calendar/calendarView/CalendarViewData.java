package com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView;

import android.util.LongSparseArray;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 14:26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewData {
    public long monthTime;  // calendar 对应的月份
    public LongSparseArray<DayType> dayDayTypeMap = new LongSparseArray<>(); // 高亮的天, dayInMillis-dayType
}
