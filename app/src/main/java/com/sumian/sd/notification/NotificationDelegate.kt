package com.sumian.sd.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.notification.INotificationDelegate
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.main.MainActivity
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/21 20:21
 * desc   :
 * version: 1.0
 */
class NotificationDelegate : INotificationDelegate {
    override fun markNotificationAsRead(notificationId: String?, data_id: Int?) {
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

    override fun getDefaultIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    override fun getUserId(): Int {
        return AppManager.getAccountViewModel().userInfo.id
    }

    override fun notifyNotificationCountChange() {
        EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
    }

    override fun uploadInstallationId(installationId: String) {
        val call = AppManager
                .getSdHttpService()
                .uploadDeviceInfo("0", installationId, Build.VERSION.SDK_INT.toString())
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse.message)
            }

            override fun onSuccess(response: Any?) {
                LogUtils.d(response)
            }

        })
    }
}