package com.sumian.sd.notification.bean

import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.NotificationUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 18:11
 * desc   :
 * version: 1.0
 */
class AppNotificationManager {
    companion object {

        fun markNotificationAsRead(intent: Intent?) {
            if (intent == null) {
                return
            }
            val notificationId = intent.getStringExtra(NotificationUtil.KEY_PUSH_NOTIFICATION_ID)
            val notificationDataId = intent.getIntExtra(NotificationUtil.KEY_PUSH_NOTIFICATION_DATA_ID, 0)
            markNotificationAsRead(notificationId, notificationDataId)
        }

        fun markNotificationAsRead(notificationId: String?, data_id: Int? = null) {
            if (TextUtils.isEmpty(notificationId)) {
                return
            }
            AppManager.getSdHttpService()
                    .readNotification(notificationId!!, data_id)
                    .enqueue(
                            object : BaseSdResponseCallback<Any>() {
                                override fun onFailure(errorResponse: ErrorResponse) {
                                    // success response body is empty, will come here -_-||
                                    EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
                                }

                                override fun onSuccess(response: Any?) {
                                    LogUtils.d("mark as read success")
                                    EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
                                }
                            })
        }
    }
}