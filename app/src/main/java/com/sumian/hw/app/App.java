package com.sumian.hw.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by jzz
 * on 2017/9/22
 * <p>
 * desc:
 */

public class App {

    public static final String TAG = App.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile Application mContext;
    private static volatile ApplicationDelegate mDelegate;

    public static void init(Application context) {
        mContext = context;
        HwAppManager.create(context);
        mDelegate = ApplicationDelegate.init().registerActivityLifecycleCallback(mContext);
    }

    public static void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mDelegate.unRegisterActivityLifecycleCallback(mContext);
        mDelegate = null;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
