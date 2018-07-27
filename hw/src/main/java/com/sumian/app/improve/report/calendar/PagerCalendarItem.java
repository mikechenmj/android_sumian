package com.sumian.app.improve.report.calendar;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.List;

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

public class PagerCalendarItem implements Comparable<PagerCalendarItem> {

    public int initTimeUnix;
    public int monthTimeUnix;
    public List<CalendarItemSleepReport> mCalendarItemSleepReports;

    @Override
    public String toString() {
        return "PagerCalendarItem{" +
            "initTimeUnix=" + initTimeUnix +
            ", monthTimeUnix=" + monthTimeUnix +
            ", mCalendarItemSleepReports=" + mCalendarItemSleepReports +
            '}';
    }

    public long getMonthTimeInMillis() {
//        return monthTimeUnix * 1000L;
        // 此处数据异常，month time 是上一个月的最后一天下午4点。加上一天修正
        return monthTimeUnix * 1000L + DateUtils.DAY_IN_MILLIS;
    }

    @Override
    public int compareTo(@NonNull PagerCalendarItem o) {
        return monthTimeUnix - o.monthTimeUnix;
    }
}
