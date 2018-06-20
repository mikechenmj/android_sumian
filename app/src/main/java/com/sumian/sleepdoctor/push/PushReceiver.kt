package com.sumian.sleepdoctor.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
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
        val pushData: PushData = PushDataResolveUtil.getPushData(intent) ?: return
        val scheme = pushData.scheme ?: return
        val notificationIntent = schemeResolver(context, scheme) ?: return
        val contentText = pushData.alert ?: return
        NotificationUtil.showNotification(context, contentText, notificationIntent)
        LogUtils.d(pushData)
    }
}
