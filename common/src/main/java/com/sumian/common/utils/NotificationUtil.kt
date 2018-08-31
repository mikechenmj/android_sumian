package com.sumian.common.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import java.util.concurrent.atomic.AtomicInteger


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 17:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class NotificationUtil {

    companion object {
        const val NOTIFICATION_ID: Int = 1000
        private const val CHANNEL_ID: String = "com.sumian.sleepdoctor"
        private const val CHANNEL_NAME: String = "com.sumian.sleepdoctor"
        private const val REQUEST_CODE = 100
        private val ATOMIC_NOTIFICATION_ID = AtomicInteger(0)

        fun getNotificationId(): Int {
            return ATOMIC_NOTIFICATION_ID.incrementAndGet()
        }

        fun areNotificationsEnabled(context: Context?): Boolean {
            if (context == null) return false
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(@DrawableRes rId: Int, context: Context, contentText: String, intent: Intent?): Int {
            createNotificationChannel(context, CHANNEL_ID, CHANNEL_NAME)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(rId)
                    .setContentTitle(AppVersionUtil.getAppName(context))
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
            val pendingIntent = getPendingIntent(context, intent)
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent)
            }
            val notificationId = getNotificationId()
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            return notificationId
        }

        private fun getPendingIntent(context: Context?, intent: Intent?): PendingIntent? {
            if (intent == null) return null
            return PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun cancelNotification(context: Context, notificationId: Int) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        fun cancelAllNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
    }
}