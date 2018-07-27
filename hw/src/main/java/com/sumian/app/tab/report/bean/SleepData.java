package com.sumian.app.tab.report.bean;

import com.sumian.app.network.response.SleepDetailReport;

/**
 * Created by jzz
 * on 2017/9/4
 * <p>
 * desc:睡眠数据特征值
 * <p>
 */
public class SleepData {

    private SleepDetailReport.SleepItem sleepItem;//每一条睡眠数据特征值
    private int timeQuantum;//数据持续时间(单位为 min)

    public SleepDetailReport.SleepItem getSleepItem() {
        return sleepItem;
    }

    public SleepData setSleepItem(SleepDetailReport.SleepItem sleepItem) {
        this.sleepItem = sleepItem;
        return this;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public SleepData setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
        return this;
    }

    @Override
    public String toString() {
        return "SleepData{" +
            "sleepItem=" + sleepItem +
            ", timeQuantum=" + timeQuantum +
            '}';
    }
}
