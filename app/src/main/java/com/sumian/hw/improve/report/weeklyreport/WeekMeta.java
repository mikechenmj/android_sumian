package com.sumian.hw.improve.report.weeklyreport;

public class WeekMeta {

    private int earliest_week;//最早周报告的开始时间 如果最早的周报告是2018-03-03-03 20：00：00 -- 2018-03-10 19：59：59 返回2018-03-04 00：00：00时间戳

    public int getEarliest_week() {
        return earliest_week;
    }

    public WeekMeta setEarliest_week(int earliest_week) {
        this.earliest_week = earliest_week;
        return this;
    }

    @Override
    public String toString() {
        return "WeekMeta{" +
            "earliest_week=" + earliest_week +
            '}';
    }
}
