package com.sumian.sd.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import com.sumian.common.lifecycle.EmptyActivityLifecycleCallbacks
import com.sumian.sd.main.WelcomeActivity

class AppInitThirdPlatformLifecycleCallback private constructor() : EmptyActivityLifecycleCallbacks(), MessageQueue.IdleHandler {

    companion object {
        @JvmStatic
        fun create(): Application.ActivityLifecycleCallbacks {
            return AppInitThirdPlatformLifecycleCallback()
        }
    }

    override fun onActivityResumed(activity: Activity?) {
        super.onActivityResumed(activity)
        if (activity is WelcomeActivity) {
            registerThirdPlatforms()
        }
    }

    override fun queueIdle(): Boolean {
        AppManager.initOnFirstActivityStart(App.getAppContext())
        return false
    }

    private fun registerThirdPlatforms() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            registerIdleHandler()
        } else {
            Handler(Looper.getMainLooper()).post {
                registerIdleHandler()
            }
        }
    }

    private fun registerIdleHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().queue.addIdleHandler(this@AppInitThirdPlatformLifecycleCallback)
        } else {
            Looper.myQueue().addIdleHandler(this@AppInitThirdPlatformLifecycleCallback)
        }
    }
}