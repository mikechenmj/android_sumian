package com.sumian.devicedemo.sleepdata.data;

import com.sumian.devicedemo.sleepdata.util.SleepDataTimeUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:睡眠报告.7天就是一周  30天就是一个月
 * <p>
 * 注意：start_date是从周六晚8点开始计算的，start_date_show是从周一0点开始计算的。
 * 推送的时候，时间是从周六晚8点开始计算的，需要注意装换
 * {
 * "id":378,
 * "start_date":"2018-04-14 20:00:00",
 * "end_date":"2018-04-21 19:59:59",
 * "avg_sleep_duration":22071,
 * "diff_avg_sleep_duration":17571,
 * "avg_awake_duration":2460,
 * "diff_avg_awake_duration":2460,
 * "avg_deep_duration":1200,
 * "diff_avg_deep_duration":0,
 * "avg_light_duration":18411,
 * "diff_avg_light_duration":15111,
 * "sleeps":Array[7],
 * "advice":null,
 * "doctors_evaluation":"111",
 * "is_read":0,
 * "start_date_show":1523721600,  // Sun Apr 15 00:00:00 CST 2018
 * "end_date_show":1524240000     // Sat Apr 21 00:00:00 CST 2018
 * },
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class SleepDurationReport implements Serializable {

    public boolean needScrollToBottom;
    public boolean isPlaceHoldData = false;
    private int id;
    private String start_date;//报告开始时间 周六20：00：00（真实记录的开始时刻）
    private int start_date_show;//报告开始时间 周日00：00：00 unix时间戳
    private String end_date;//报告结束时间 周日19：59：59 unix时间戳（真实记录的结束时刻
    private int end_date_show;//报告结束时间 周日00：00：00 unix时间戳
    private Integer avg_sleep_duration;//日均睡眠时长
    private Integer diff_avg_sleep_duration;//日均睡眠时长比上周时长变化
    private Integer avg_awake_duration;//日均夜醒时长
    private Integer diff_avg_awake_duration;//日均夜醒时长比上周时长变化
    private Integer avg_deep_duration;//日均深睡时长
    private Integer diff_avg_deep_duration;//日均深睡时长比上周时长变化
    private Integer avg_light_duration;//日均浅睡时长
    private Integer diff_avg_light_duration;//日均浅睡时长比上周时长变化
    private List<SleepDuration> sleeps;//周报告图表，数组下标 0 - 6 对应星期天到星期六； 0-28/29/30/31  对应一个月
    private SleepAdvice advice;//睡眠建议
    private String doctors_evaluation;//医生评价
    private int is_read;//1：已读，0：未读

    public static SleepDurationReport createFromTime(long timeMillis) {
        long currentStartTime = SleepDataTimeUtil.getStartTimeOfWeek(timeMillis);
        long currentEndTime = SleepDataTimeUtil.getWeekEndDayTime(timeMillis);
        SleepDurationReport weekReport = new SleepDurationReport();
        weekReport.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(currentStartTime)));
        weekReport.setStart_date_show((int) (currentStartTime / 1000));
        weekReport.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(currentEndTime)));
        weekReport.setEnd_date_show((int) (currentEndTime / 1000));
        return weekReport;
    }

    /**
     * @param timeInMillis any time of the week of the report
     * @return An empty report with specific start time and end time
     */
    public static SleepDurationReport createPlaceHoldData(long timeInMillis) {
        SleepDurationReport sleepDurationReport = createFromTime(timeInMillis);
        sleepDurationReport.isPlaceHoldData = true;
        return sleepDurationReport;
    }

    public Integer getAvg_sleep_duration() {
        return avg_sleep_duration;
    }

    public SleepDurationReport setAvg_sleep_duration(Integer avg_sleep_duration) {
        this.avg_sleep_duration = avg_sleep_duration;
        return this;
    }

    public Integer getDiff_avg_sleep_duration() {
        return diff_avg_sleep_duration;
    }

    public SleepDurationReport setDiff_avg_sleep_duration(Integer diff_avg_sleep_duration) {
        this.diff_avg_sleep_duration = diff_avg_sleep_duration;
        return this;
    }

    public Integer getAvg_awake_duration() {
        return avg_awake_duration;
    }

    public SleepDurationReport setAvg_awake_duration(Integer avg_awake_duration) {
        this.avg_awake_duration = avg_awake_duration;
        return this;
    }

    public Integer getDiff_avg_awake_duration() {
        return diff_avg_awake_duration;
    }

    public SleepDurationReport setDiff_avg_awake_duration(Integer diff_avg_awake_duration) {
        this.diff_avg_awake_duration = diff_avg_awake_duration;
        return this;
    }

    public Integer getAvg_deep_duration() {
        return avg_deep_duration;
    }

    public SleepDurationReport setAvg_deep_duration(Integer avg_deep_duration) {
        this.avg_deep_duration = avg_deep_duration;
        return this;
    }

    public Integer getDiff_avg_deep_duration() {
        return diff_avg_deep_duration;
    }

    public SleepDurationReport setDiff_avg_deep_duration(Integer diff_avg_deep_duration) {
        this.diff_avg_deep_duration = diff_avg_deep_duration;
        return this;
    }

    public Integer getAvg_light_duration() {
        return avg_light_duration;
    }

    public SleepDurationReport setAvg_light_duration(Integer avg_light_duration) {
        this.avg_light_duration = avg_light_duration;
        return this;
    }

    public Integer getDiff_avg_light_duration() {
        return diff_avg_light_duration;
    }

    public SleepDurationReport setDiff_avg_light_duration(Integer diff_avg_light_duration) {
        this.diff_avg_light_duration = diff_avg_light_duration;
        return this;
    }

    public List<SleepDuration> getSleeps() {
        return sleeps;
    }

    public SleepDurationReport setSleeps(List<SleepDuration> sleeps) {
        this.sleeps = sleeps;
        return this;
    }

    public SleepAdvice getAdvice() {
        return advice;
    }

    public SleepDurationReport setAdvice(SleepAdvice advice) {
        this.advice = advice;
        return this;
    }

    public String getDoctors_evaluation() {
        return doctors_evaluation;
    }

    public void setDoctors_evaluation(String doctors_evaluation) {
        this.doctors_evaluation = doctors_evaluation;
    }

    public int getId() {
        return id;
    }

    public SleepDurationReport setId(int id) {
        this.id = id;
        return this;
    }

    public String getStart_date() {
        return start_date;
    }

    public SleepDurationReport setStart_date(String start_date) {
        this.start_date = start_date;
        return this;
    }

    public int getStart_date_show() {
        return start_date_show;
    }

    /**
     * @param start_date_show unix time
     */
    public SleepDurationReport setStart_date_show(int start_date_show) {
        this.start_date_show = start_date_show;
        return this;
    }

    public long getStartDateShowInMillis() {
        return getStart_date_show() * 1000L;
    }

    public SleepDurationReport setStartDateShowInMillis(long timeInMillis) {
        start_date_show = (int) (timeInMillis / 1000);
        return this;
    }

    public SleepDurationReport setEndDateShowInMillis(long timeInMillis) {
        end_date_show = (int) (timeInMillis / 1000);
        return this;
    }

    public String getEnd_date() {
        return end_date;
    }

    public SleepDurationReport setEnd_date(String end_date) {
        this.end_date = end_date;
        return this;
    }

    public int getEnd_date_show() {
        return end_date_show;
    }

    /**
     * @param end_date_show unix time
     */
    public SleepDurationReport setEnd_date_show(int end_date_show) {
        this.end_date_show = end_date_show;
        return this;
    }

    public int getIs_read() {
        return is_read;
    }

    public SleepDurationReport setIs_read(int is_read) {
        this.is_read = is_read;
        return this;
    }

    public boolean isTimeBetweenStartAndEnd(long unixTime) {
        return start_date_show * 1000L <= unixTime && unixTime <= end_date_show * 1000L;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepDurationReport{" +
                "id=" + id +
                ", start_date='" + start_date + '\'' +
                ", start_date_show=" + start_date_show +
                ", end_date='" + end_date + '\'' +
                ", end_date_show=" + end_date_show +
                ", avg_sleep_duration=" + avg_sleep_duration +
                ", diff_avg_sleep_duration=" + diff_avg_sleep_duration +
                ", avg_awake_duration=" + avg_awake_duration +
                ", diff_avg_awake_duration=" + diff_avg_awake_duration +
                ", avg_deep_duration=" + avg_deep_duration +
                ", diff_avg_deep_duration=" + diff_avg_deep_duration +
                ", avg_light_duration=" + avg_light_duration +
                ", diff_avg_light_duration=" + diff_avg_light_duration +
                ", sleeps=" + sleeps +
                ", advice=" + advice +
                ", doctors_evaluation='" + doctors_evaluation + '\'' +
                ", is_read=" + is_read +
                '}';
    }
}
