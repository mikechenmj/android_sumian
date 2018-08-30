package com.sumian.sd.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.sumian.hw.log.LogJobIntentService

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/27 10:46
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ActivityLifecycleCallbackForUploadLog : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        LogJobIntentService.uploadLogIfNeed(activity)
    }

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
    }
}