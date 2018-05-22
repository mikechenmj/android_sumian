package com.sumian.open;

import android.content.Context;

import com.sumian.open.analytics.OpenAnalytics;
import com.sumian.open.login.OpenLogin;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by jzz
 * on 2017/12/27.
 * desc:
 */

public class OpenEngine {

    private OpenLogin mOpenLogin;
    private OpenAnalytics mOpenAnalytics;

    public OpenEngine register(Context context, boolean isDebug,String appId,String appSecret) {
        MobclickAgent.setCatchUncaughtExceptions(true);
        this.mOpenLogin = new OpenLogin().init(context, isDebug,appId,appSecret);
        this.mOpenAnalytics = new OpenAnalytics().init(context, isDebug);
        return this;
    }

    public OpenLogin getOpenLogin() {
        return mOpenLogin;
    }

    public OpenAnalytics getOpenAnalytics() {
        return mOpenAnalytics;
    }
}
