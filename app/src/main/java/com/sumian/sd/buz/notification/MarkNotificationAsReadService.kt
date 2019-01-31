package com.sumian.sd.buz.notification

import android.app.IntentService
import android.content.Intent
import com.sumian.common.notification.AppNotificationManager
import com.sumian.sd.app.AppManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/20 18:12
 * desc   :
 * version: 1.0
 */
class MarkNotificationAsReadService : IntentService("MarkNotificationAsReadService") {
    override fun onHandleIntent(intent: Intent?) {
        if(!AppManager.getAccountViewModel().isLogin) {
           return
        }
        AppNotificationManager.markNotificationAsRead(intent)
    }
}