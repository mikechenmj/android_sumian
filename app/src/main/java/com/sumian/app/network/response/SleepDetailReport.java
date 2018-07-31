package com.sumian.app.network.response;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:  某一段睡眠的睡眠详细信息
 */

public class SleepDetailReport {

    private int id;  //主键
    private int user_id;//用户id
    private int from_time;//睡眠数据开始采集时间戳
    private int to_time;//睡眠数据结束采集时间戳
    private int sleep_at;//入睡时间
    private int waked_up_at;//睡醒时间
    private int sleep_duration;//睡眠时长
    private int awake_duration;//夜醒时长
    private int deep_duration;//深睡时长
    private int light_duration;//浅睡时长
    private int wake_up_mood;//苏醒情绪  0不太好. 1一般般. 2还可以. 3.好极了.
    private List<String> bedtime_state;//睡前状态
    private String remark;//睡眠备注
    private String wrote_diary_at;//填写睡眠日记时间
    private String doctors_evaluation;//医生评价，为空字符串表示医生未评价
    private int is_read;//医生评价是否已读，0：未读，1：已读；医生填写评价后要把标志改为 0
    private int is_popped;//睡眠日记是否已弹窗，0：未弹，1：已弹
    private SleepPackage packages;//睡眠数据包

    public int getId() {
        return id;
    }

    public SleepDetailReport setId(int id) {
        this.id = id;
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public SleepDetailReport setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public int getFrom_time() {
        return from_time;
    }

    public SleepDetailReport setFrom_time(int from_time) {
        this.from_time = from_time;
        return this;
    }

    public int getTo_time() {
        return to_time;
    }

    public SleepDetailReport setTo_time(int to_time) {
        this.to_time = to_time;
        return this;
    }

    public int getSleep_at() {
        return sleep_at;
    }

    public SleepDetailReport setSleep_at(int sleep_at) {
        this.sleep_at = sleep_at;
        return this;
    }

    public int getWaked_up_at() {
        return waked_up_at;
    }

    public SleepDetailReport setWaked_up_at(int waked_up_at) {
        this.waked_up_at = waked_up_at;
        return this;
    }

    public int getSleep_duration() {
        return sleep_duration;
    }

    public SleepDetailReport setSleep_duration(int sleep_duration) {
        this.sleep_duration = sleep_duration;
        return this;
    }

    public int getAwake_duration() {
        return awake_duration;
    }

    public SleepDetailReport setAwake_duration(int awake_duration) {
        this.awake_duration = awake_duration;
        return this;
    }

    public int getDeep_duration() {
        return deep_duration;
    }

    public SleepDetailReport setDeep_duration(int deep_duration) {
        this.deep_duration = deep_duration;
        return this;
    }

    public int getLight_duration() {
        return light_duration;
    }

    public SleepDetailReport setLight_duration(int light_duration) {
        this.light_duration = light_duration;
        return this;
    }

    public int getWake_up_mood() {
        return wake_up_mood;
    }

    public SleepDetailReport setWake_up_mood(int wake_up_mood) {
        this.wake_up_mood = wake_up_mood;
        return this;
    }

    public List<String> getBedtime_state() {
        return bedtime_state;
    }

    public SleepDetailReport setBedtime_state(List<String> bedtime_state) {
        this.bedtime_state = bedtime_state;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public SleepDetailReport setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getWrote_diary_at() {
        return wrote_diary_at;
    }

    public SleepDetailReport setWrote_diary_at(String wrote_diary_at) {
        this.wrote_diary_at = wrote_diary_at;
        return this;
    }

    public String getDoctors_evaluation() {
        return doctors_evaluation;
    }

    public SleepDetailReport setDoctors_evaluation(String doctors_evaluation) {
        this.doctors_evaluation = doctors_evaluation;
        return this;
    }

    public int getIs_read() {
        return is_read;
    }

    public SleepDetailReport setIs_read(int is_read) {
        this.is_read = is_read;
        return this;
    }

    public int getIs_popped() {
        return is_popped;
    }

    public SleepDetailReport setIs_popped(int is_popped) {
        this.is_popped = is_popped;
        return this;
    }

    public SleepPackage getPackages() {
        return packages;
    }

    public SleepDetailReport setPackages(SleepPackage packages) {
        this.packages = packages;
        return this;
    }

    @Override
    public String toString() {
        return "SleepDetailReport{" +
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
            ", wake_up_mood=" + wake_up_mood +
            ", bedtime_state=" + bedtime_state +
            ", remark='" + remark + '\'' +
            ", wrote_diary_at='" + wrote_diary_at + '\'' +
            ", doctors_evaluation='" + doctors_evaluation + '\'' +
            ", is_read=" + is_read +
            ", is_popped=" + is_popped +
            ", packages=" + packages +
            '}';
    }

    public static class SleepPackage {

        private List<SleepItem> data;

        public List<SleepItem> getData() {
            return data;
        }

        public SleepPackage setData(List<SleepItem> data) {
            this.data = data;
            return this;
        }

        @Override
        public String toString() {
            return "SleepPackage{" +
                "data=" + data +
                '}';
        }
    }

    public static class SleepItem {
        private int index;
        private int count;
        private int from_time;
        private int to_time;
        private int state;

        public int getIndex() {
            return index;
        }

        public SleepItem setIndex(int index) {
            this.index = index;
            return this;
        }

        public int getCount() {
            return count;
        }

        public SleepItem setCount(int count) {
            this.count = count;
            return this;
        }

        public int getFrom_time() {
            return from_time;
        }

        public SleepItem setFrom_time(int from_time) {
            this.from_time = from_time;
            return this;
        }

        public int getTo_time() {
            return to_time;
        }

        public SleepItem setTo_time(int to_time) {
            this.to_time = to_time;
            return this;
        }

        public int getState() {
            return state;
        }

        public SleepItem setState(int state) {
            this.state = state;
            return this;
        }

        @Override
        public String toString() {
            return "SleepItem{" +
                "index=" + index +
                ", count=" + count +
                ", from_time=" + from_time +
                ", to_time=" + to_time +
                ", state=" + state +
                '}';
        }
    }

}


