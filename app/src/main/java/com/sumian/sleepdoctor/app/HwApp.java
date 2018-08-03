package com.sumian.sleepdoctor.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by jzz
 * on 2017/9/22
 * <p>
 * desc:
 */

public class HwApp {

    public static final String TAG = HwApp.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile Application mContext;
    private static volatile HwApplicationDelegate mDelegate;

    public static void init(Application context) {
        mContext = context;
        HwAppManager.create(context);
        mDelegate = HwApplicationDelegate.init().registerActivityLifecycleCallback(mContext);
    }

    public static void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mDelegate.unRegisterActivityLifecycleCallback(mContext);
        mDelegate = null;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
