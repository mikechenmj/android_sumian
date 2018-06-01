package com.sumian.sleepdoctor.sleepRecord.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 9:14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepPill implements Parcelable {

    /**
     * name : 艾司唑仑
     * amount : 2.75片
     * time : 午饭前／后
     */

    private String name;
    private String amount;
    private String time;

    private SleepPill(Parcel in) {
        name = in.readString();
        amount = in.readString();
        time = in.readString();
    }

    public static final Creator<SleepPill> CREATOR = new Creator<SleepPill>() {
        @Override
        public SleepPill createFromParcel(Parcel in) {
            return new SleepPill(in);
        }

        @Override
        public SleepPill[] newArray(int size) {
            return new SleepPill[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(amount);
        dest.writeString(time);
    }
}
