package com.sumian.sd.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityLifecycleCallbackForUserAnalysis : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
        AppManager.getOpenAnalytics().onPageEnd(activity?.javaClass?.simpleName)
        AppManager.getOpenAnalytics().onPause(activity)
    }

    override fun onActivityResumed(activity: Activity?) {
        AppManager.getOpenAnalytics().onPageStart(activity?.javaClass?.simpleName)
        AppManager.getOpenAnalytics().onResume(activity)
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}