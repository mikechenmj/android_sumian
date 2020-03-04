package com.sumian.common.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sumian.common.log.CommonLog
import com.sumian.common.log.CommonLogManager
import java.util.logging.LogManager


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

    override fun onReceive(context: Context?, intent: Intent?) {
        CommonLog.log("PushReceiver.onReceive: $context $intent")
        if (context == null || intent == null) {
            return
        }
        val pushData: PushData = PushDataResolveUtil.getPushData(intent) ?: return
        AppNotificationManager.showNotificationIfPossible(context, pushData)
    }
}
