package com.sumian.sd.network.response;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public class UserSetting {

    public int user_id;//用户 id
    public int sleep_diary_enable;//消息通知-睡眠日记，0：关闭，1：开启

    @Override
    public String toString() {
        return "UserSetting{" +
            "user_id=" + user_id +
            ", sleep_diary_enable=" + sleep_diary_enable +
            '}';
    }
}
