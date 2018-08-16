package com.sumian.sd.account.config;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.sumian.sd.account.captcha.CaptchaTime;
import com.sumian.sd.app.App;
import com.sumian.sd.utils.SpUtil;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:app 相关配置文件
 */

public final class SumianConfig {

    public static final String LOGIN_CAPTCHA_TYPE = "login_captcha_type";
    public static final String BIND_SOCIAL_CAPTCHA_TYPE = "bind_social_captcha_type";
    private static final String SETTING_NAME = "sumian_doctor_setting";
    public static final int PASSWORD_LENGTH_MIN = 6;
    public static final int PASSWORD_LENGTH_MAX = 16;
    public static final int REAL_NAME_LENGTH_MIN = 2;
    public static final int REAL_NAME_LENGTH_MAX = 16;
    public static final int NICK_NAME_LENGTH_MIN = 1;
    public static final int NICK_NAME_LENGTH_MAX = 16;
    public static final int SEND_CAPTCHA_COLD_TIME = 30;
    public static final int CAPTCHA_LENGTH = 6;

    public static void updateCaptchaTimeDistance(CaptchaTime captchaTime, String captchaTimeType) {
        String json = JSON.toJSONString(captchaTime);
        SharedPreferences.Editor editor = initEdit();
        editor.putString(captchaTimeType, json);
        SpUtil.apply(editor);
    }

    public static CaptchaTime syncCaptchaTimeDistance(String captchaTimeType) {
        String json = initSp().getString(captchaTimeType, null);
        if (TextUtils.isEmpty(json)) {
            return new CaptchaTime();
        }
        return JSON.parseObject(json, CaptchaTime.class);
    }

    public static void clearCaptchaTimeDistance(String captchaTimeType) {
        SharedPreferences.Editor editor = initSp().edit().remove(captchaTimeType);
        SpUtil.apply(editor);
    }

    public static void clear() {
        SharedPreferences.Editor editor = initEdit();
        editor.clear();
        SpUtil.apply(editor);
    }

    private static SharedPreferences initSp() {
        return SpUtil.initSp(App.Companion.getAppContext(), SETTING_NAME);
    }

    private static SharedPreferences.Editor initEdit() {
        return SpUtil.initEdit(App.Companion.getAppContext(), SETTING_NAME);
    }

}
