package com.sumian.sd.diary.sleeprecord.bean;

import android.text.TextUtils;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 9:43
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecord {
    /**
     * id : 838
     * date : 1527782400
     * answer : {"bed_at":"23:00","sleep_at":"23:00","wake_up_at":"07:00","get_up_at":"07:00","wake_times":2,"wake_minutes":30,"energetic":4,"sleepless_factor":["饮酒","喝茶/咖啡","身体不适","吃太饱","有心事","睡前运动过量"],"other_sleep_times":1,"other_sleep_total_minutes":15,"sleep_pills":[{"name":"唑吡坦","amount":"1片","time":"早饭前／后"},{"name":"唑吡坦","amount":"1片","time":"午饭前／后"},{"name":"咪达唑仑","amount":"1.75片","time":"午饭前／后"},{"name":"硝西泮","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"}],"remark":"我昨晚睡得很好^_^"}
     * sleep_duration : 27000
     * fall_asleep_duration : 0
     * sleep_efficiency : 94
     * doctor_evaluation :
     * created_at : 1527815172
     * updated_at : 1527815617
     */

    private int id;
    private int date;
    private SleepRecordAnswer answer;
    private int on_bed_duration;
    private int sleep_duration;
    private int fall_asleep_duration;
    private int sleep_efficiency;
    private String doctor_evaluation;
    private int created_at;
    private int updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public SleepRecordAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(SleepRecordAnswer answer) {
        this.answer = answer;
    }

    public int getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(int sleep_duration) {
        this.sleep_duration = sleep_duration;
    }

    public int getFall_asleep_duration() {
        return fall_asleep_duration;
    }

    public void setFall_asleep_duration(int fall_asleep_duration) {
        this.fall_asleep_duration = fall_asleep_duration;
    }

    public int getSleep_efficiency() {
        return sleep_efficiency;
    }

    public void setSleep_efficiency(int sleep_efficiency) {
        this.sleep_efficiency = sleep_efficiency;
    }

    public String getDoctor_evaluation() {
        return doctor_evaluation;
    }

    public void setDoctor_evaluation(String doctor_evaluation) {
        this.doctor_evaluation = doctor_evaluation;
    }

    public boolean hasDoctorEvaluation() {
        return !TextUtils.isEmpty(doctor_evaluation);
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public int getOn_bed_duration() {
        return on_bed_duration;
    }

    public void setOn_bed_duration(int on_bed_duration) {
        this.on_bed_duration = on_bed_duration;
    }

    @Override
    public String toString() {
        return "SleepResultBean{" +
                "id=" + id +
                ", date=" + date +
                ", answer=" + answer +
                ", sleep_duration=" + sleep_duration +
                ", fall_asleep_duration=" + fall_asleep_duration +
                ", sleep_efficiency=" + sleep_efficiency +
                ", doctor_evaluation='" + doctor_evaluation + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
