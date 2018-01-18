package com.sumian.sleepdoctor.account.bean;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class Token {

    public String token;
    public int expired_at;
    public int refresh_expired_at;
    public UserProfile user;
    public boolean is_new;

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expired_at=" + expired_at +
                ", refresh_expired_at=" + refresh_expired_at +
                ", user=" + user +
                ", is_new=" + is_new +
                '}';
    }
}
