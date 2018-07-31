package com.sumian.app.account.bean;

/**
 * Created by sm
 * on 2018/3/14.
 * desc:
 */

public class Answer {

    public int id;
    public String answers;//睡眠障碍评估表 答案
    public int score;//睡眠障碍评估表 得分
    public int level;//失眠程度 0：睡眠质量好，1：轻度失眠，2：中度失眠，3：重度失眠
    public int created_at;//填写时间

    @Override
    public String toString() {
        return "Answer{" +
            "id=" + id +
            ", answers='" + answers + '\'' +
            ", score=" + score +
            ", level=" + level +
            ", created_at=" + created_at +
            '}';
    }
}
