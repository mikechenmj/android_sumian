package com.sumian.sd.network.response;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc: 日睡眠数据报告
 */

public class DaySleepReport {

    private long id;//睡眠信息 ID
    private long user_id;//用户 ID
    private int from_time;//睡眠特征数据采集时间
    private int to_time;//睡眠特征数据采集结束时间
    private int sleep_at;//入睡时间，为 null 表示解析不出来
    private int waked_up_at;//睡醒时间，为 null 表示解析不出来
    private int sleep_duration;//睡眠时长
    private int awake_duration;//夜醒时长
    private int deep_duration;//深睡时长
    private int light_duration;//浅睡时长
    private String doctors_evaluation;//医生评价，为空字符串表示医生未评价
    private int is_read;//医生评价是否已读，0：未读，1：已读；医生填写评价后要把标志改为 0
    private int is_popped;//睡眠日记是否已弹窗，0：未弹，1：已弹


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getFrom_time() {
        return from_time;
    }

    public void setFrom_time(int from_time) {
        this.from_time = from_time;
    }

    public int getTo_time() {
        return to_time;
    }

    public void setTo_time(int to_time) {
        this.to_time = to_time;
    }

    public int getSleep_at() {
        return sleep_at;
    }

    public DaySleepReport setSleep_at(int sleep_at) {
        this.sleep_at = sleep_at;
        return this;
    }

    public int getWaked_up_at() {
        return waked_up_at;
    }

    public DaySleepReport setWaked_up_at(int waked_up_at) {
        this.waked_up_at = waked_up_at;
        return this;
    }

    public int getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(int sleep_duration) {
        this.sleep_duration = sleep_duration;
    }

    public int getAwake_duration() {
        return awake_duration;
    }

    public void setAwake_duration(int awake_duration) {
        this.awake_duration = awake_duration;
    }

    public int getDeep_duration() {
        return deep_duration;
    }

    public void setDeep_duration(int deep_duration) {
        this.deep_duration = deep_duration;
    }

    public int getLight_duration() {
        return light_duration;
    }

    public void setLight_duration(int light_duration) {
        this.light_duration = light_duration;
    }

    public String getDoctors_evaluation() {
        return doctors_evaluation;
    }

    public void setDoctors_evaluation(String doctors_evaluation) {
        this.doctors_evaluation = doctors_evaluation;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getIs_popped() {
        return is_popped;
    }

    public void setIs_popped(int is_popped) {
        this.is_popped = is_popped;
    }

    @Override
    public String toString() {
        return "DaySleepReport{" +
            "id=" + id +
            ", user_id=" + user_id +
            ", from_time=" + from_time +
            ", to_time=" + to_time +
            ", sleep_at=" + sleep_at +
            ", waked_up_at=" + waked_up_at +
            ", sleep_duration=" + sleep_duration +
            ", awake_duration=" + awake_duration +
            ", deep_duration=" + deep_duration +
            ", light_duration=" + light_duration +
            ", doctors_evaluation='" + doctors_evaluation + '\'' +
            ", is_read=" + is_read +
            ", is_popped=" + is_popped +
            '}';
    }
}
