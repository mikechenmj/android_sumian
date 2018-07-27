package com.sumian.app.network.request;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */


public class CaptchaBody {

    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public CaptchaBody setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String toString() {
        return "CaptchaBody{" +
            "getLeanCloudId='" + mobile + '\'' +
            '}';
    }
}
