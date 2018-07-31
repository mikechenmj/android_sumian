package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class HwToken {

    private String token;
    private int expired_at;
    private int refresh_expired_at;
    private HwUserInfo mUserInfo;

    public String getToken() {
        return token;
    }

    public HwToken setToken(String token) {
        this.token = token;
        return this;
    }

    public int getExpired_at() {
        return expired_at;
    }

    public HwToken setExpired_at(int expired_at) {
        this.expired_at = expired_at;
        return this;
    }

    public int getRefresh_expired_at() {
        return refresh_expired_at;
    }

    public HwToken setRefresh_expired_at(int refresh_expired_at) {
        this.refresh_expired_at = refresh_expired_at;
        return this;
    }

    public HwUserInfo getUserInfo() {
        return mUserInfo;
    }

    public HwToken setUserInfo(HwUserInfo userInfo) {
        this.mUserInfo = userInfo;
        return this;
    }

    @Override
    public String toString() {
        return "HwToken{" +
            "token='" + token + '\'' +
            ", expired_at=" + expired_at +
            ", refresh_expired_at=" + refresh_expired_at +
            ", mUserInfo=" + mUserInfo +
            '}';
    }
}
