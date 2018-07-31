package com.sumian.app.network.request;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ResetPwdBody {

    private String mobile;
    private String password;
    private String password_confirmation;
    private String ticket;

    public String getMobile() {
        return mobile;
    }

    public ResetPwdBody setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ResetPwdBody setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public ResetPwdBody setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
        return this;
    }

    public String getTicket() {
        return ticket;
    }

    public ResetPwdBody setTicket(String ticket) {
        this.ticket = ticket;
        return this;
    }

    @Override
    public String toString() {
        return "ResetPwdBody{" +
            "getLeanCloudId='" + mobile + '\'' +
            ", password='" + password + '\'' +
            ", password_confirmation='" + password_confirmation + '\'' +
            ", ticket='" + ticket + '\'' +
            '}';
    }
}
