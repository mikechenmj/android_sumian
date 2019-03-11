package com.sumian.common.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVInstallation
import com.avos.avoscloud.SaveCallback
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 18:11
 * desc   :
 * version: 1.0
 */
object AppNotificationManager {
    private const val KEY_PUSH_NOTIFICATION_ID = "com.sumian.common.notification.AppNotificationManager.push_notification_id"
    private const val KEY_PUSH_NOTIFICATION_DATA_ID = "com.sumian.common.notification.AppNotificationManager.push_notification_data_id"
    private const val REQUEST_CODE = 100

    private lateinit var mNotificationDelegate: INotificationDelegate
    private lateinit var mSchemeResolver: ISchemeResolver
    private lateinit var mChannelId: String
    private lateinit var mChannelName: String
    private var mSmallIcon: Int = 0
    private var mLargeIcon: Int = 0
    private var mUserIdKey = ""


    fun init(context: Context,
             smallIcon: Int, largeIcon: Int,
             channelId: String, channelName: String,
             notificationDelegate: INotificationDelegate,
             schemeResolver: ISchemeResolver,
             userIdKey:String) {
        NotificationUtil.createNotificationChannel(context, channelId, channelName)
        mSmallIcon = smallIcon
        mLargeIcon = largeIcon
        mChannelId = channelId
        mChannelName = channelName
        mNotificationDelegate = notificationDelegate
        mSchemeResolver = schemeResolver
        mUserIdKey = userIdKey
    }

    fun markNotificationAsRead(intent: Intent?) {
        intent?.let {
            val notificationId = it.getStringExtra(KEY_PUSH_NOTIFICATION_ID)
            val notificationDataId = it.getIntExtra(KEY_PUSH_NOTIFICATION_DATA_ID, 0)
            markNotificationAsRead(notificationId, notificationDataId)
        }
    }

    private fun markNotificationAsRead(notificationId: String?, data_id: Int? = null) {
        if (TextUtils.isEmpty(notificationId)) {
            return
        }
        mNotificationDelegate.markNotificationAsRead(notificationId, data_id)
    }

    fun showNotificationIfPossible(context: Context, pushData: PushData) {
        LogUtils.d("pushData", pushData)
        val scheme = pushData.scheme ?: return
        if (!isUserIdValid(scheme)) {
            LogUtils.d("push data user id invalid")
            return
        }
        val notificationId = SchemeResolveUtil.getNotificationIdFromScheme(scheme) ?: ""
        val notificationDataId = SchemeResolveUtil.getNotificationDataIdFromScheme(scheme)
        val intent = mSchemeResolver.schemeResolver(context, scheme)
                ?: mNotificationDelegate.getDefaultIntent(context)
        intent.putExtra(KEY_PUSH_NOTIFICATION_ID, notificationId)
        intent.putExtra(KEY_PUSH_NOTIFICATION_DATA_ID, notificationDataId)
        val contentText = pushData.alert ?: return
        NotificationUtil.showNotification(
                context,
                mChannelId,
                mSmallIcon,
                mLargeIcon,
                AppUtils.getAppName(), contentText,
                getPendingIntent(context, intent)
        )
        mNotificationDelegate.notifyNotificationCountChange()
    }

    fun uploadPushId() {
        AVInstallation.getCurrentInstallation().saveInBackground(object : SaveCallback() {
            override fun done(p0: AVException?) {
                val installationId = AVInstallation.getCurrentInstallation().installationId
                LogUtils.d("installationId", installationId)
                mNotificationDelegate.uploadInstallationId(installationId)
            }
        })
    }

    private fun getPendingIntent(context: Context?, intent: Intent?): PendingIntent? {
        if (intent == null) return null
        return PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun isUserIdValid(scheme: String): Boolean {
        val userIdStr = SchemeResolveUtil.getParamFromScheme(scheme, mUserIdKey)
        if (TextUtils.isEmpty(userIdStr)) {
            return true
        }
        val pushUserId = Integer.valueOf(userIdStr!!)
        val id = mNotificationDelegate.getUserId()
        if (pushUserId != id) {
            LogUtils.d("user id not equal")
            return false
        }
        return true
    }
}

interface INotificationDelegate {
    fun markNotificationAsRead(notificationId: String?, data_id: Int? = null)

    fun getDefaultIntent(context: Context): Intent

    fun getUserId(): Int

    fun notifyNotificationCountChange()

    fun uploadInstallationId(installationId: String)
}

interface ISchemeResolver {
    fun schemeResolver(context: Context, scheme: String): Intent?
}