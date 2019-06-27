package com.sumian.sd.buz.account.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jzz
 * on 2018/1/23.
 * desc:
 */

public class Token {

    public String token;
    public @SerializedName("expired_at")
    int expired_at;
    public @SerializedName("refresh_expired_at")
    int refresh_expired_at;
    public @SerializedName("user")
    UserInfo user;
//    public @SerializedName("is_new")
//    boolean is_new;//true:新用户，false:旧用户

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expired_at=" + expired_at +
                ", refresh_expired_at=" + refresh_expired_at +
                ", user=" + user +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(int expired_at) {
        this.expired_at = expired_at;
    }

    public int getRefresh_expired_at() {
        return refresh_expired_at;
    }

    public void setRefresh_expired_at(int refresh_expired_at) {
        this.refresh_expired_at = refresh_expired_at;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isNew() {
        return user != null && user.last_login_at == null;
    }
}
