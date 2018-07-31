package com.sumian.hw.improve.guideline.utils;

import android.content.SharedPreferences;

import com.sumian.hw.common.util.SpUtil;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public final class GuidelineUtils {

    private static final String USER_GUIDE_SETTING = "user_guide_setting";
    public static final String SP_KEY_NEED_SHOW_DAILY_REPORT_USER_GUIDE = "need_show_daily_report_user_guide";
    public static final String SP_KEY_NEED_SHOW_WELCOME_USER_GUIDE = "need_show_welcome_user_guide";

    public static boolean needShowDailyUserGuide() {
        return initUserGuideSp().getBoolean(SP_KEY_NEED_SHOW_DAILY_REPORT_USER_GUIDE, true);
    }

    public static boolean needShowWelcomeUserGuide() {
        return initUserGuideSp().getBoolean(SP_KEY_NEED_SHOW_WELCOME_USER_GUIDE, true);
    }

    private static SharedPreferences initUserGuideSp() {
        return SpUtil.initSp(USER_GUIDE_SETTING);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = initUserGuideSp();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }
}
