package com.sumian.sd.notification.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/11/5 14:05
 * desc   : 点击notification 只能放一个intent，不能做额外逻辑，如发起网络请求标记消息已读。
 * 为了实现该需求，点击notification时把数据通过广播发送到该receiver中，在此跳转页面，同时标记消息已读。
 * version: 1.0
 */
class LaunchIntentReceiver : BroadcastReceiver() {
    companion object {
        const val KEY_ACTION = "com.sumian.action.LaunchIntent"
        const val KEY_LAUNCH_INTENT = "launch_intent"
        const val KEY_NOTIFICATION_ID = "notification_id"

        fun getIntent(notificationId: String, launchIntent: Intent): Intent {
            val intent = Intent(LaunchIntentReceiver.KEY_ACTION)
            intent.putExtra(LaunchIntentReceiver.KEY_NOTIFICATION_ID, notificationId)
            intent.putExtra(LaunchIntentReceiver.KEY_LAUNCH_INTENT, launchIntent)
            return intent
        }

        fun registerLaunchIntentReceiver(context: Context) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(KEY_ACTION)
            context.registerReceiver(LaunchIntentReceiver(), intentFilter)
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        LogUtils.d("onReceive")
        val launchIntent = intent.getParcelableExtra<Intent>(KEY_LAUNCH_INTENT) ?: return
        val notificationId = intent.getStringExtra(KEY_NOTIFICATION_ID) ?: return
        ActivityUtils.startActivity(launchIntent)
        markNotificationAsRead(notificationId)
    }

    private fun markNotificationAsRead(notificationId: String) {
        if (TextUtils.isEmpty(notificationId)) {
            return
        }
        AppManager.getSdHttpService().readNotification(notificationId = notificationId).enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d("mark as read fail", errorResponse)
            }

            override fun onSuccess(response: Any?) {
                LogUtils.d("mark as read success")
                EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
            }
        })
    }
}