package com.sumian.sleepdoctor.sleepRecord.util;

import android.text.format.DateUtils;

import com.sumian.sleepdoctor.sleepRecord.bean.SleepData;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewData;
import com.sumian.sleepdoctor.widget.calendar.calendarView.DayType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 21:39
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepDataToCalendarDataUtil {
    // TYPE_0 普通天
    // TYPE_1 当天
    // TYPE_2 未来的天
    // TYPE_3 有数据，无评价
    // TYPE_4 有数据，有评价
    // TYPE_5 当前选择的天

    public static CalendarViewData getCalendarViewData(long monthTime, long currentTime,
                                                       long currentSelectTime, List<SleepData> sleepDataList) {
        List<Long> hasSleepDataDays = new ArrayList<>();
        List<Long> hasDoctorEvaluationDays = new ArrayList<>();
        for (SleepData sleepData : sleepDataList) {
            long sleepDataTime = sleepData.getDateInMillis();
            hasSleepDataDays.add(sleepDataTime);
            if (sleepData.isHasDoctorsEvaluation()) {
                hasDoctorEvaluationDays.add(sleepDataTime);
            }
        }
        long currentDayStartTime = TimeUtil.getDayStartTime(currentTime);
        Calendar calendar = TimeUtil.getStartDayOfMonth(monthTime);
        monthTime = calendar.getTimeInMillis();
        int dayCount = TimeUtil.getDayCountInTheMonth(monthTime);
        CalendarViewData calendarViewData = new CalendarViewData();
        calendarViewData.monthTime = monthTime;
        for (int i = 0; i < dayCount; i++) {
            long dayTime = monthTime + DateUtils.DAY_IN_MILLIS * i;
            DayType dayType = DayType.TYPE_0;
            if (dayTime == currentDayStartTime) {
                dayType = DayType.TYPE_1;
            } else if (dayTime > currentDayStartTime) {
                dayType = DayType.TYPE_2;
            }
            if (hasSleepDataDays.contains(dayTime)) {
                dayType = hasDoctorEvaluationDays.contains(dayTime) ? DayType.TYPE_4 : DayType.TYPE_3;
            }
            if (dayTime == currentSelectTime) {
                dayType = DayType.TYPE_5;
            }
            calendarViewData.dayDayTypeMap.put(dayTime, dayType);
        }
        return calendarViewData;
    }

    public static List<CalendarViewData> sleepResponseToCalendarViewData(Map<String, List<SleepData>> response,
                                                                         long currentTime, long currentSelectTime) {
        currentTime = TimeUtil.getDayStartTime(currentTime);
        currentSelectTime = TimeUtil.getDayStartTime(currentSelectTime);
        if (response == null) {
            return null;
        }
        List<CalendarViewData> list = new ArrayList<>();
        Set<Map.Entry<String, List<SleepData>>> entries = response.entrySet();
        for (Map.Entry<String, List<SleepData>> entry : entries) {
            long monthTime = Integer.valueOf(entry.getKey()) * 1000L;
            CalendarViewData calendarViewData = getCalendarViewData(monthTime, currentTime, currentSelectTime, entry.getValue());
            list.add(calendarViewData);
        }
        return list;
    }

}
