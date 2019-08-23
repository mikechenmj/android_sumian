package com.sumian.sd.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.sumian.common.social.analytics.ActivityLifecycleCallbackForUserAnalysis
import com.sumian.sd.common.log.SdLogManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Process
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService


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
        if (getProcessName(Process.myPid()) == packageName) {
            AppManager.initOnAppStart(this)
            registerActivityLifecycleCallbacks(ActivityLifecycleCallbackForUserAnalysis())
            registerActivityLifecycleCallbacks(LogActivityLifecycleCallbacks())
            SdLogManager.log("App onCreate")
        }
    }

    private fun getProcessName(pid: Int): String {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid === pid) {
                return processInfo.processName
            }
        }
        return ""
    }
}

