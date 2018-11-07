package com.sumian.hw.report.weeklyreport;

import android.support.annotation.NonNull;

/**
 * Created by jzz
 * on 2018/3/5.
 * desc:
 */

public class CalendarItemSleepReport {

    public int id;//日报告id
    public int date;//unix time 某一天,只要有日报告都会显示,如2018-02-01 00:00:00
    public boolean is_read;//是否已读
    @SuppressWarnings("WeakerAccess")
    public boolean is_today;//是否是今天

    @NonNull
    @Override
    public String toString() {
        return "CalendarItemSleepReport{" +
            "id=" + id +
            ", date=" + date +
            ", is_read=" + is_read +
            ", is_today=" + is_today +
            '}';
    }

    public long getDateInMillis() {
        return date * 1000L;
    }
}
