package com.sumian.sddoctor.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.notification.INotificationDelegate
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.EventBusUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/22 18:56
 * desc   :
 * version: 1.0
 */
class NotificationDelegate : INotificationDelegate {
    override fun getDefaultIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    override fun getUserId(): Int {
        return AppManager.getAccountViewModel().getDoctorInfo().value?.id ?: 0
    }

    override fun markNotificationAsRead(notificationId: String?, data_id: Int?) {
        if (TextUtils.isEmpty(notificationId)) {
            return
        }
        AppManager.getHttpService().readNotification(notificationId!!, data_id).enqueue(object : BaseSdResponseCallback<Any>() {
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

    override fun notifyNotificationCountChange() {
        EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
    }

    override fun uploadInstallationId(installationId: String) {
        if (AppManager.getAccountViewModel().isVisitorAccount()) return
        val call = AppManager
                .getHttpService()
                .portables("0", installationId, Build.VERSION.SDK_INT.toString())
        call.enqueue(object : BaseSdResponseCallback<Any?>() {
            override fun onSuccess(response: Any?) {
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse)
            }
        })
    }
}