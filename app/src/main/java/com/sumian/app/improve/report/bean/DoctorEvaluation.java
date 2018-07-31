package com.sumian.app.improve.report.bean;

import java.util.List;

/**
 * Created by jzz
 * on 2018/3/12.
 * desc:  医生建议
 */

public class DoctorEvaluation {

    public int wake_up_mood;//苏醒情绪，-1：未填写，0：不太好，1：一般般，2：还可以，3：好极了
    public List<String> bedtime_state;//睡前状态
    public String remark;//睡眠备注
    public String wrote_diary_at;//填写睡眠日记时间，为 NULL 时表示用户未填写睡眠日记

    @Override
    public String toString() {
        return "DoctorEvaluation{" +
            "wake_up_mood=" + wake_up_mood +
            ", bedtime_state=" + bedtime_state +
            ", remark='" + remark + '\'' +
            ", wrote_diary_at='" + wrote_diary_at + '\'' +
            '}';
    }
}
