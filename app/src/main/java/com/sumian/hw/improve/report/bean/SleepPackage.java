package com.sumian.hw.improve.report.bean;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:睡眠数据包
 */

public class SleepPackage {

    public int id;//包id
    public int sleep_id;//睡眠 id
    public int index;//包索引
    public int from_time;//此条数据表示的起始时间
    public int to_time;//此条数据表示的结束时间
    public int state;//状态 0：清醒，1：REM，2：浅睡，3：深睡
    public int count;//包数量
    public int duration;//一次睡眠状态持续时间

    public void calculateDuration() {
        duration = to_time - from_time;
    }

    @Override
    public String toString() {
        return "SleepPackage{" +
            "id=" + id +
            ", sleep_id=" + sleep_id +
            ", index=" + index +
            ", from_time=" + from_time +
            ", to_time=" + to_time +
            ", state=" + state +
            ", count=" + count +
            ", duration=" + duration +
            '}';
    }
}
