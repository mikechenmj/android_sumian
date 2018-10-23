package com.sumian.common.social.analytics

import android.app.Activity
import com.sumian.common.lifecycle.EmptyActivityLifecycleCallbacks
import com.umeng.analytics.MobclickAgent

class ActivityLifecycleCallbackForUserAnalysis : EmptyActivityLifecycleCallbacks() {
    override fun onActivityPaused(activity: Activity?) {
        super.onActivityPaused(activity)
        MobclickAgent.onPageEnd(activity?.javaClass?.simpleName)
        MobclickAgent.onPause(activity)
    }

    override fun onActivityResumed(activity: Activity?) {
        super.onActivityResumed(activity)
        MobclickAgent.onPageStart(activity?.javaClass?.simpleName)
        MobclickAgent.onResume(activity)
    }
}