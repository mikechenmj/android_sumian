package com.sumian.hw.report.dailyreport;

public class DailyMeta {

    private int earliest_day;//用户使用设备后最早一条数据的时间戳,如果最早一条是2018-03-03 则返回2018-03-03 00：00：00时间戳

    @Override
    public String toString() {
        return "DailyMeta{" +
            "earliest_day=" + earliest_day +
            '}';
    }
}
