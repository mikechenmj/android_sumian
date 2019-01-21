package com.sumian.sd.app

import android.app.Application
import com.sumian.common.social.analytics.ActivityLifecycleCallbackForUserAnalysis
import com.sumian.sd.log.SdLogManager

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

class App : Application() {
    companion object {
        @Volatile
        private lateinit var mAppContext: Application

        @JvmStatic
        fun getAppContext() = mAppContext
    }

    override fun onCreate() {
        super.onCreate()
        mAppContext = this
        SdLogManager.log("App onCreate")
        AppManager.initOnAppStart(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbackForUserAnalysis())
        registerActivityLifecycleCallbacks(LogActivityLifecycleCallbacks())
    }
}

