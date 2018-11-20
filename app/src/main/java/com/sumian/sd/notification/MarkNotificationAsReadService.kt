package com.sumian.sd.notification

import android.app.IntentService
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.sumian.sd.app.AppManager
import com.sumian.sd.notification.bean.AppNotificationManager

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