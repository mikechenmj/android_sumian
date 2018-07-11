package com.sumian.sleepdoctor.record.bean;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 9:10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FillSleepRecordResponse {
    /**
     * code : 0
     * sleepRecord : {"id":838,"date":1527782400,"answer":{"bed_at":"23:00","sleep_at":"23:00","wake_up_at":"07:00","get_up_at":"07:00","wake_times":2,"wake_minutes":30,"energetic":4,"sleepless_factor":["饮酒","喝茶/咖啡","身体不适","吃太饱","有心事","睡前运动过量"],"other_sleep_times":1,"other_sleep_total_minutes":15,"sleep_pills":[{"name":"唑吡坦","amount":"1片","time":"早饭前／后"},{"name":"唑吡坦","amount":"1片","time":"午饭前／后"},{"name":"咪达唑仑","amount":"1.75片","time":"午饭前／后"},{"name":"硝西泮","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"}],"remark":"我昨晚睡得很好^_^"},"sleep_duration":27000,"fall_asleep_duration":0,"sleep_efficiency":94,"doctor_evaluation":"","created_at":1527815172,"updated_at":1527815617}
     */

    private int code;
    @SerializedName("result")
    private SleepRecord sleepRecord;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SleepRecord getSleepRecord() {
        return sleepRecord;
    }

    public void setSleepRecord(SleepRecord sleepRecord) {
        this.sleepRecord = sleepRecord;
    }

    @Override
    public String toString() {
        return "FillSleepRecordResponse{" +
                "code=" + code +
                ", sleepRecord=" + sleepRecord +
                '}';
    }
}
