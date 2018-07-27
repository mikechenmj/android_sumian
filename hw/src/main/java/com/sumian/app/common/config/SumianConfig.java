package com.sumian.app.common.config;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.sumian.app.common.captcha.CaptchaTime;
import com.sumian.app.common.util.SpUtil;
import com.sumian.app.network.response.Reminder;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:app 相关配置文件
 */

public final class SumianConfig {

    private static final String TAG = SumianConfig.class.getSimpleName();

    public static final String REGISTER_CAPTCHA_KEY = "register_captcha";
    public static final String FORGET_PWD_CAPTCHA_KEY = "forget_pwd_captcha_key";
    private static final String SETTING_NAME = "sumian_setting";
    private static final String TG_CONNECTED_HISTORY_STATE = "tg_connected_history_state";
    private static final String REMINDER_KEY = "reminder_key";

    public static void updateTgConnectedCacheState(boolean isOn) {
        SharedPreferences.Editor editor = initEdit().putBoolean(TG_CONNECTED_HISTORY_STATE, isOn);
        SpUtil.apply(editor);
    }

    public static boolean getTgConnectedCacheState() {
        return initSp().getBoolean(TG_CONNECTED_HISTORY_STATE, false);
    }

    public static void updateReminder(Reminder reminder) {
        //Log.e(TAG, "updateReminder: ------->" + reminder.toString());
        if (reminder == null) return;
        String json = JSON.toJSONString(reminder);
        SharedPreferences.Editor editor = initEdit();
        editor.putString(REMINDER_KEY, json);
        SpUtil.apply(editor);
    }

    public static Reminder getReminder() {
        String json = initSp().getString(REMINDER_KEY, null);
        if (TextUtils.isEmpty(json)) return null;
        return JSON.parseObject(json, Reminder.class);
    }

    public static void updateCaptchaTimeDistance(CaptchaTime captchaTime, String captchaTimeType) {
        String json = JSON.toJSONString(captchaTime);
        SharedPreferences.Editor editor = initEdit();
        editor.putString(captchaTimeType, json);
        SpUtil.apply(editor);
    }

    public static CaptchaTime syncCaptchaTimeDistance(String captchaTimeType) {
        String json = initSp().getString(captchaTimeType, null);
        if (TextUtils.isEmpty(json))
            return new CaptchaTime();
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
        return SpUtil.initSp(SETTING_NAME);
    }

    private static SharedPreferences.Editor initEdit() {
        return SpUtil.initEdit(SETTING_NAME);
    }

}
