package com.sumian.sd.common.h5.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.sumian.sd.R

class H5KeepAliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(H5AudioNotificationConst.H5_MUSIC_CHANNEL_ID,
                    H5AudioNotificationConst.H5_MUSIC_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            Notification.Builder(this, H5AudioNotificationConst.H5_MUSIC_CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        val notification: Notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name) + getString(R.string.music_playing_tip))
                .build()
        startForeground(H5AudioNotificationConst.H5_MUSIC_ID, notification)
    }
}