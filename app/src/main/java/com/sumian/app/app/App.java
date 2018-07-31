package com.sumian.app.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.sumian.app.app.delegate.ApplicationDelegate;

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
