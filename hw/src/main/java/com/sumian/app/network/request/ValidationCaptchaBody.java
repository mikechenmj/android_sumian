package com.sumian.app.network.request;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:校验验证码  body
 */

public class ValidationCaptchaBody {

    private String mobile;
    private String captcha;

    public String getMobile() {
        return mobile;
    }

    public ValidationCaptchaBody setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public ValidationCaptchaBody setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    @Override
    public String toString() {
        return "ValidationCaptchaBody{" +
            "getLeanCloudId='" + mobile + '\'' +
            ", doCaptcha='" + captcha + '\'' +
            '}';
    }
}
