package com.sumian.sddoctor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.AppUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent
import com.sumian.sddoctor.notification.MarkNotificationAsReadService
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
        const val KEY_PUSH_NOTIFICATION_ID = "push_notification_id"
        const val KEY_PUSH_NOTIFICATION_DATA_ID = "push_notification_data_id"

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

        fun showNotification(context: Context,
                             contentText: String,
                             notificationId: String,
                             notificationDataId: Int?,
                             intent: Intent?): Int {
            createNotificationChannel(context, CHANNEL_ID, CHANNEL_NAME)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentTitle(AppUtils.getAppName())
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
            val pendingIntent = if (intent != null) {
                intent.putExtra(KEY_PUSH_NOTIFICATION_ID, notificationId)
                intent.putExtra(KEY_PUSH_NOTIFICATION_DATA_ID, notificationDataId)
                getPendingIntent(context, intent)
            } else {
                getMarkNotificationAsReadService(context, notificationId, notificationDataId)
            }
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent)
            }
            val nId = getNotificationId()
            NotificationManagerCompat.from(context).notify(nId, builder.build())
            EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
            return nId
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

        private fun getMarkNotificationAsReadService(context: Context, notificationId: String,
                                                     notificationDataId: Int?): PendingIntent? {
            val intent = Intent(context, MarkNotificationAsReadService::class.java)
            intent.putExtra(NotificationUtil.KEY_PUSH_NOTIFICATION_ID, notificationId)
            intent.putExtra(NotificationUtil.KEY_PUSH_NOTIFICATION_DATA_ID, notificationDataId)
            return PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }
}