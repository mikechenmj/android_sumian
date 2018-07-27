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

public class App extends Application {

    public static final String TAG = App.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile RequestManager mRequestManager;

    private ApplicationDelegate mDelegate;

    @SuppressLint("StaticFieldLeak")
    private static volatile Context mContext;

    public static RequestManager getRequestManager() {
        return mRequestManager;
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.create(this);
        AppOperator.runOnThread(() -> {
            if (mContext == null) {
                mContext = getApplicationContext();
            }

            if (mRequestManager == null)
                mRequestManager = Glide.with(this);

            if (mDelegate == null) {
                this.mDelegate = ApplicationDelegate.init().registerActivityLifecycleCallback(this);
            }
        });
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);
        this.mDelegate.unRegisterActivityLifecycleCallback(this);
        this.mDelegate = null;
    }

}
