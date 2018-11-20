package com.sumian.sd.notification.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.sd.app.AppManager
import com.sumian.sd.main.MainActivity
import com.sumian.sd.utils.NotificationUtil


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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

    override fun onReceive(context: Context, intent: Intent) {
        val pushData: PushData = PushDataResolveUtil.getPushData(intent) ?: return
        val scheme = pushData.scheme ?: return
        LogUtils.d(pushData)
        if (!isUserIdValid(scheme)) {
            LogUtils.d("push data user id invalid")
            return
        }
        val notificationId = SchemeResolveUtil.getNotificationIdFromScheme(scheme) ?: ""
        val notificationDataId = SchemeResolveUtil.getNotificationDataIdFromScheme(scheme)
        val notificationIntent = SchemeResolveUtil.schemeResolver(context, scheme)
        val contentText = pushData.alert ?: return
        NotificationUtil.showNotification(context, contentText, notificationId, notificationDataId, notificationIntent)
    }

    private fun isUserIdValid(scheme: String): Boolean {
        val userIdStr = SchemeResolveUtil.getUserIdFromScheme(scheme)
        if (TextUtils.isEmpty(userIdStr)) {
            return true
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
