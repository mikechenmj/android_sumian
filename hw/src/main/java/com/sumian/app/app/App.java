package com.sumian.app.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sumian.app.app.delegate.ApplicationDelegate;
import com.sumian.app.common.operator.AppOperator;

/**
 * Created by jzz
 * on 2017/9/22
 * <p>
 * desc:
 */

public class App {

    public static final String TAG = App.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile RequestManager mRequestManager;
    private static volatile Application mContext;
    private static volatile ApplicationDelegate mDelegate;

    public static RequestManager getRequestManager() {
        return mRequestManager;
    }

    public static void init(Application context) {
        mContext = context;
        AppManager.create(context);
        AppOperator.runOnThread(() -> {
            if (mContext == null) {
                mContext = context;
            }
            if (mRequestManager == null) {
                mRequestManager = Glide.with(context);
            }
            if (mDelegate == null) {
                mDelegate = ApplicationDelegate.init().registerActivityLifecycleCallback(mContext);
            }
        });
    }

    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mDelegate.unRegisterActivityLifecycleCallback(mContext);
        mDelegate = null;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
