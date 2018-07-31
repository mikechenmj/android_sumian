package com.sumian.hw.common.captcha;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:  验证码时间戳
 */

public class CaptchaTime {

    private int remainingTime;
    private long saveTime;

    public int getRemainingTime() {
        return remainingTime;
    }

    public CaptchaTime setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
        return this;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public CaptchaTime setSaveTime(long saveTime) {
        this.saveTime = saveTime;
        return this;
    }

    @Override
    public String toString() {
        return "CaptchaTime{" +
            "remainingTime=" + remainingTime +
            ", saveTime=" + saveTime +
            '}';
    }
}
