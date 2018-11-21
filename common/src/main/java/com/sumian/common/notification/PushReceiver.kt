package com.sumian.common.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


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
        AppNotificationManager.showNotificationIfPossible(context, pushData)
    }
}
