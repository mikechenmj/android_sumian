package com.sumian.open.analytics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jzz
 * on 2017/12/27.
 * desc:
 */

public class OpenAnalytics {

    public OpenAnalytics init(Context context, boolean isDebug) {
        MobclickAgent.setDebugMode(isDebug);
        MobclickAgent.enableEncrypt(true);// 设置是否对日志信息进行加密, 默认false(不加密).
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        return this;
    }

    public OpenAnalytics onResume(Context context) {
        MobclickAgent.onResume(context);       //统计时长
        return this;
    }

    public OpenAnalytics onPause(Context context) {
        MobclickAgent.onPause(context);
        return this;
    }

    public OpenAnalytics onPageStart(String tag) {
        MobclickAgent.onPageStart(tag);
        return this;
    }

    public OpenAnalytics onPageEnd(String tag) {
        MobclickAgent.onPageEnd(tag);
        return this;
    }

    public void onClickEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    public void onClickEvent(Context context, String eventId, boolean isOn) {
        Map<String, String> map = new HashMap<>();
        map.put("sleep_reminder_is_on", "" + isOn);
        MobclickAgent.onEvent(context, eventId, map);
    }

    public void onProfileSignIn(String userId) {
        MobclickAgent.onProfileSignIn(userId);
    }

    public void onProfileSignIn(String Provider, String userId) {
        MobclickAgent.onProfileSignIn(Provider, userId);
    }

    public void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }
}
