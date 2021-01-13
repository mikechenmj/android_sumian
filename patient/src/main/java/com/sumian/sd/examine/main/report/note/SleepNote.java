package com.sumian.sd.examine.main.report.note;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jzz
 * on 2018/3/21.
 * desc:
 */

public class SleepNote implements Serializable {

    public int sleepId;//当前睡眠报告 id
    public int wakeUpMood;//苏醒情绪，-1：未填写，0：不太好，1：一般般，2：还可以，3：好极了 如果id=0 没有此字段
    public List<String> bedtimeState;//睡前状态
    public String remark;//睡前备注

    @Override
    public String toString() {
        return "BedtimeState{" +
            "sleepId=" + sleepId +
            ", wakeUpMood=" + wakeUpMood +
            ", bedtimeState=" + bedtimeState +
            ", remark='" + remark + '\'' +
            '}';
    }
}
