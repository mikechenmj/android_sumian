package com.sumian.hw.network.request;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:  doLogin request body
 */

public class LoginBody {

    private String mobile;
    private String password;

    public String getMobile() {
        return mobile;
    }

    public LoginBody setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginBody setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "LoginBody{" +
            "getLeanCloudId='" + mobile + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
