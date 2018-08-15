package com.sumian.sd.record.bean;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 9:47
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordAnswer {
    /**
     * bed_at : 23:00
     * sleep_at : 23:00
     * wake_up_at : 07:00
     * get_up_at : 07:00
     * wake_times : 2
     * wake_minutes : 30
     * energetic : 4
     * sleepless_factor : ["饮酒","喝茶/咖啡","身体不适","吃太饱","有心事","睡前运动过量"]
     * other_sleep_times : 1
     * other_sleep_total_minutes : 15
     * sleep_pills : [{"name":"唑吡坦","amount":"1片","time":"早饭前／后"},{"name":"唑吡坦","amount":"1片","time":"午饭前／后"},{"name":"咪达唑仑","amount":"1.75片","time":"午饭前／后"},{"name":"硝西泮","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"}]
     * remark : 我昨晚睡得很好^_^
     */

    private String bed_at;
    private String sleep_at;
    private String wake_up_at;
    private String get_up_at;
    private int wake_times;
    private int wake_minutes;
    private int energetic;
    private int other_sleep_times;
    private int other_sleep_total_minutes;
    private String remark;
    private List<String> sleepless_factor;
    private List<SleepPill> sleep_pills;

    public String getBed_at() {
        return bed_at;
    }

    public void setBed_at(String bed_at) {
        this.bed_at = bed_at;
    }

    public String getSleep_at() {
        return sleep_at;
    }

    public void setSleep_at(String sleep_at) {
        this.sleep_at = sleep_at;
    }

    public String getWake_up_at() {
        return wake_up_at;
    }

    public void setWake_up_at(String wake_up_at) {
        this.wake_up_at = wake_up_at;
    }

    public String getGet_up_at() {
        return get_up_at;
    }

    public void setGet_up_at(String get_up_at) {
        this.get_up_at = get_up_at;
    }

    public int getWake_times() {
        return wake_times;
    }

    public void setWake_times(int wake_times) {
        this.wake_times = wake_times;
    }

    public int getWake_minutes() {
        return wake_minutes;
    }

    public void setWake_minutes(int wake_minutes) {
        this.wake_minutes = wake_minutes;
    }

    public int getEnergetic() {
        return energetic;
    }

    public void setEnergetic(int energetic) {
        this.energetic = energetic;
    }

    public int getOther_sleep_times() {
        return other_sleep_times;
    }

    public void setOther_sleep_times(int other_sleep_times) {
        this.other_sleep_times = other_sleep_times;
    }

    public int getOther_sleep_total_minutes() {
        return other_sleep_total_minutes;
    }

    public void setOther_sleep_total_minutes(int other_sleep_total_minutes) {
        this.other_sleep_total_minutes = other_sleep_total_minutes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getSleepless_factor() {
        return sleepless_factor;
    }

    public void setSleepless_factor(List<String> sleepless_factor) {
        this.sleepless_factor = sleepless_factor;
    }

    public List<SleepPill> getSleep_pills() {
        return sleep_pills;
    }

    public void setSleep_pills(List<SleepPill> sleep_pills) {
        this.sleep_pills = sleep_pills;
    }
}
