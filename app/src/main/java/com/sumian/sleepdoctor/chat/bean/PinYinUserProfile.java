package com.sumian.sleepdoctor.chat.bean;

import android.support.annotation.NonNull;

import com.sumian.sleepdoctor.account.bean.UserProfile;

/**
 * Created by jzz
 * on 2018/2/23.
 * desc:
 */

public class PinYinUserProfile implements Comparable<PinYinUserProfile>{

    public String pinyin;
    public String firstChar;
    public UserProfile userProfile;

    @Override
    public String toString() {
        return "PinYinUserProfile{" +
                "pinyin='" + pinyin + '\'' +
                ", firstChar='" + firstChar + '\'' +
                ", userProfile=" + userProfile +
                '}';
    }

    @Override
    public int compareTo(@NonNull PinYinUserProfile o) {
        return firstChar.compareTo(o.firstChar);
    }
}
