package com.sumian.sd.main

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.sumian.common.notification.AppNotificationManager
import com.sumian.sd.app.AppManager.getSleepDataUploadManager
import com.sumian.sd.app.AppManager.sendHeartbeat
import com.sumian.sd.app.AppManager.syncUserInfo
import com.sumian.sd.buz.kefu.KefuManager

/**
 * Created by sm
 *
 * on 2018/12/25
 *
 * desc:
 *
 */
class MainLazyInitHelper {

    companion object {

        @JvmStatic
        fun create(): MainLazyInitHelper {
            return MainLazyInitHelper()
        }
    }

    fun initLazyMainService() {
        prepareLooper()
    }

    private fun prepareLooper() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            registerIdleHandler()
        } else {
            Handler(Looper.getMainLooper()).post {
                registerIdleHandler()
            }
        }
    }

    private fun initMainPlatform() {
        KefuManager.loginAndQueryUnreadMsg()
        AppNotificationManager.uploadPushId()
        getSleepDataUploadManager().checkPendingTaskAndRun()
        sendHeartbeat()
        syncUserInfo()
    }

    private fun registerIdleHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().queue.addIdleHandler {
                initMainPlatform()
                false
            }
        } else {
            Looper.myQueue().addIdleHandler {
                initMainPlatform()
                false
            }
        }
    }
}