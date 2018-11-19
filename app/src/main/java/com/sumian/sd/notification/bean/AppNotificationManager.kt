package com.sumian.sd.notification.bean

import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 18:11
 * desc   :
 * version: 1.0
 */
class AppNotificationManager {
    companion object {

        fun markNotificationAsRead(notificationId: String?, data_id: Int? = null) {
            if (TextUtils.isEmpty(notificationId)) {
                return
            }
            AppManager.getSdHttpService()
                    .readNotification(notificationId!!, data_id)
                    .enqueue(
                            object : BaseSdResponseCallback<Any>() {
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
}