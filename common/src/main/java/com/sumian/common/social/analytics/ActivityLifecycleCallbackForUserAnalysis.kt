package com.sumian.common.social.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.umeng.analytics.MobclickAgent

class ActivityLifecycleCallbackForUserAnalysis : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
        MobclickAgent.onPageEnd(activity?.javaClass?.simpleName)
        MobclickAgent.onPause(activity)
    }

    override fun onActivityResumed(activity: Activity?) {
        MobclickAgent.onPageStart(activity?.javaClass?.simpleName)
        MobclickAgent.onResume(activity)
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