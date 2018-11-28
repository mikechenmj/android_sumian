package com.sumian.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
object NotificationUtil {

    private const val REQUEST_CODE = 100
    private val ATOMIC_NOTIFICATION_ID = AtomicInteger(0)

    private fun getNotificationId(): Int {
        return ATOMIC_NOTIFICATION_ID.incrementAndGet()
    }

    fun areNotificationsEnabled(context: Context?): Boolean {
        if (context == null) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context?,
                         channelId: String,
                         smallIcon: Int,
                         largeIcon: Int,
                         title: String,
                         contentText: String,
                         pendingIntent: PendingIntent?) {
        if (context == null) return
        val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        if (pendingIntent != null) builder.setContentIntent(pendingIntent)
        NotificationManagerCompat.from(context).notify(getNotificationId(), builder.build())
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