package com.sumian.app.improve.report.bean;

import java.util.List;

/**
 * Created by jzz
 * on 2018/3/5.
 * desc:  睡眠日记
 */

public class Diary {

    public int id;
    public int user_id;
    public int date;
    public int sleep_duration;//睡眠时长
    public int awake_duration;//清醒时长
    public int deep_duration;//深睡时长
    public int light_duration;//浅睡时长
    public String wake_up_mood;//苏醒情绪或睡眠状态
    public List<String> bedtime_state;//睡前状态
    public String remark;//睡眠备注
    public String wrote_diary_at;//填写睡眠日记时间，为 NULL 时表示用户未填写睡眠日记
    public String doctors_evaluation;//医生评价，为空字符串表示医生未评价
    public int is_read;//医生评价是否已读，0：未读，1：已读；医生填写评价后要把标志改为 0
    public SleepPackage packages;//睡眠数据包

    @Override
    public String toString() {
        return "Diary{" +
            "id=" + id +
            ", user_id=" + user_id +
            ", sleep_duration=" + sleep_duration +
            ", awake_duration=" + awake_duration +
            ", deep_duration=" + deep_duration +
            ", light_duration=" + light_duration +
            ", wake_up_mood='" + wake_up_mood + '\'' +
            ", bedtime_state='" + bedtime_state + '\'' +
            ", remark='" + remark + '\'' +
            ", wrote_diary_at='" + wrote_diary_at + '\'' +
            ", doctors_evaluation='" + doctors_evaluation + '\'' +
            ", is_read=" + is_read +
            ", packages=" + packages +
            '}';
    }

}
