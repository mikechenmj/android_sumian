package com.sumian.sd.examine.login.bean;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:  doBindMonitor request body
 */

public class RegisterBody {

    private String mobile;
    private String password;
    private String captcha;

    public String getMobile() {
        return mobile;
    }

    public RegisterBody setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterBody setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public RegisterBody setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterBody{" +
            "getLeanCloudId='" + mobile + '\'' +
            ", password='" + password + '\'' +
            ", doCaptcha='" + captcha + '\'' +
            '}';
    }
}
