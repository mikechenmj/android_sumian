package com.sumian.sleepdoctor.notification.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.utils.NotificationUtil


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 10:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PushReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.d(intent?.action)
        if (context == null) return
        if (intent == null) return
        val pushData: PushData = PushDataResolveUtil.getPushData(intent)
                ?: return
        val scheme = pushData.scheme ?: return
        if (!isUserIdValid(scheme)) {
            return
        }
        val notificationIntent = SchemeResolveUtil.schemeResolver(context, scheme)
                ?: return
        val contentText = pushData.alert ?: return
        NotificationUtil.showNotification(context, contentText, notificationIntent)
        LogUtils.d(pushData)
    }

    private fun isUserIdValid(scheme: String): Boolean {
        val userIdStr = SchemeResolveUtil.getUserIdFromScheme(scheme)
        if (TextUtils.isEmpty(userIdStr)) {
            return false
        }
        val pushUserId = Integer.valueOf(userIdStr)
        val id = AppManager.getAccountViewModel().userInfo.id
        if (pushUserId != id) {
            LogUtils.d("user id not equal")
            return false
        }
        return true
    }
}
