package com.sumian.sleepdoctor.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import com.blankj.utilcode.util.LogUtils
import com.sumian.sleepdoctor.BuildConfig
import com.sumian.sleepdoctor.R
import android.support.v4.app.NotificationManagerCompat
import com.sumian.sleepdoctor.main.MainActivity
import com.sumian.sleepdoctor.utils.NotificationUtil
import java.net.URLDecoder


/**
 * <pre>
 *     author : Zhan Xuzhao
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
        LogUtils.d(pushData)
        val notificationIntent =
                PushSchemeResolver.schemeResolver(context, pushData.scheme ?: return) ?: return
        NotificationUtil.showNotification(context,
                pushData.alert ?: return,
                notificationIntent)
    }
}