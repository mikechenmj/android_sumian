package com.sumian.app.network.response;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class Token {

    private String token;
    private int expired_at;
    private int refresh_expired_at;
    private UserInfo mUserInfo;

    public String getToken() {
        return token;
    }

    public Token setToken(String token) {
        this.token = token;
        return this;
    }

    public int getExpired_at() {
        return expired_at;
    }

    public Token setExpired_at(int expired_at) {
        this.expired_at = expired_at;
        return this;
    }

    public int getRefresh_expired_at() {
        return refresh_expired_at;
    }

    public Token setRefresh_expired_at(int refresh_expired_at) {
        this.refresh_expired_at = refresh_expired_at;
        return this;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public Token setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
        return this;
    }

    @Override
    public String toString() {
        return "Token{" +
            "token='" + token + '\'' +
            ", expired_at=" + expired_at +
            ", refresh_expired_at=" + refresh_expired_at +
            ", mUserInfo=" + mUserInfo +
            '}';
    }
}
