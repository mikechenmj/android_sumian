package com.sumian.hw.report.bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by jzz
 * on 2018/3/5.
 * desc:日报告
 */

@SuppressWarnings("WeakerAccess")
public class DailyReport implements Comparable<DailyReport> {

    public int id;
    public int user_id;
    public int date;//unix时间戳：1,519,833,600，不传默认返回最新
    public int sleep_duration;//睡眠时长
    public int awake_duration;//清醒时长
    public int deep_duration;//深睡时长
    public int light_duration;//浅睡时长
    public int light_duration_percent;//浅睡占全天睡眠时长比例*100，如显示64%，则返回64
    public int deep_duration_percent;//深睡占全天睡眠时长比例*100，如显示64%，则返回64
    public int wake_up_mood;//苏醒情绪，-1：未填写，0：不太好，1：一般般，2：还可以，3：好极了
    public ArrayList<String> bedtime_state;//睡前状态
    public String remark;//睡眠备注
    public String wrote_diary_at;//填写睡眠日记时间，为 NULL 时表示用户未填写睡眠日记
    public String doctors_evaluation;//医生评价，为空字符串表示医生未评价
    public int is_read;//医生评价是否已读，0：未读，1：已读；医生填写评价后要把标志改为 0
    public ArrayList<SleepPackage> packages;//睡眠数据包
    public boolean needScrollToBottom = false;

    @Override
    public String toString() {
        return "SleepSegment{" +
            "id=" + id +
            ", user_id=" + user_id +
            ", date=" + date +
            ", sleep_duration=" + sleep_duration +
            ", awake_duration=" + awake_duration +
            ", deep_duration=" + deep_duration +
            ", light_duration=" + light_duration +
            ", light_duration_percent=" + light_duration_percent +
            ", deep_duration_percent=" + deep_duration_percent +
            ", wake_up_mood=" + wake_up_mood +
            ", bedtime_state=" + bedtime_state +
            ", remark='" + remark + '\'' +
            ", wrote_diary_at='" + wrote_diary_at + '\'' +
            ", doctors_evaluation='" + doctors_evaluation + '\'' +
            ", is_read=" + is_read +
            ", packages=" + packages +
            '}';
    }

    @Override
    public int compareTo(@NonNull DailyReport o) {
        return date - o.date;
    }

    public long getDateInMillis() {
        return date * 1000L;
    }
}
