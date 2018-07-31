package com.sumian.hw.network.response;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:睡眠提醒功能
 */

public class Reminder {

    private int id;//提醒订阅 id
    private int reminder_id;//提醒类型 ID，1：睡眠提醒
    private int user_id;//用户 id
    private String remind_at;//提醒时间，格式：20:00:00
    private int enable;//是否开启，0：不开启，1：开启

    public int getId() {
        return id;
    }

    public Reminder setId(int id) {
        this.id = id;
        return this;
    }

    public int getReminder_id() {
        return reminder_id;
    }

    public Reminder setReminder_id(int reminder_id) {
        this.reminder_id = reminder_id;
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public Reminder setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getRemind_at() {
        return remind_at;
    }

    public Reminder setRemind_at(String remind_at) {
        this.remind_at = remind_at;
        return this;
    }

    public int getEnable() {
        return enable;
    }

    public Reminder setEnable(int enable) {
        this.enable = enable;
        return this;
    }

    public int getReminderHour() {
        String remindAt = this.getRemind_at();
        return Integer.parseInt(String.format(Locale.getDefault(), "%s", TextUtils.isEmpty(remindAt) ? "00" : remindAt.substring(0, 2)));
    }

    public int getReminderMin() {
        String remindAt = this.getRemind_at();
        return Integer.parseInt(String.format(Locale.getDefault(), "%s", TextUtils.isEmpty(remindAt) ? "00" : remindAt.substring(3, 5)));
    }

    public String getReminderFormatTime() {
        return String.format(Locale.getDefault(), "%02d%s%02d", getReminderHour(), ":", getReminderMin());
    }

    @Override
    public String toString() {
        return "Reminder{" +
            "id=" + id +
            ", reminder_id=" + reminder_id +
            ", user_id=" + user_id +
            ", remind_at='" + remind_at + '\'' +
            ", enable=" + enable +
            '}';
    }
}
