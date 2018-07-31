package com.sumian.hw.improve.main.bean;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PushReport implements Serializable, Cloneable {
    @IntDef({PUSH_TYPE_DAILY_REPORT, PUSH_TYPE_WEEKLY_REPORT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PushType {}

    public static final int PUSH_TYPE_DAILY_REPORT = 0x01;
    public static final int PUSH_TYPE_WEEKLY_REPORT = 0x02;

    public static final String EXTRA_PUSH_REPORT = "com.sumian.app.extra_PUSH_REPORT";

    private int pushDate;   // second
    private int pushType;

    public int getPushDate() {
        return pushDate;
    }

    public void setPushDate(int pushDate) {
        this.pushDate = pushDate;
    }

    @PushType
    public int getPushType() {
        return pushType;
    }

    public void setPushType(@PushType int pushType) {
        this.pushType = pushType;
    }

    @Override
    public String toString() {
        return "PushReport{" +
            "pushDate=" + pushDate +
            ", pushType=" + pushType +
            '}';
    }

    @Override
    public PushReport clone() throws CloneNotSupportedException {
        return (PushReport) super.clone();
    }
}
