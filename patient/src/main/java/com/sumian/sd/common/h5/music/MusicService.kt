package com.sumian.sd.common.h5.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.ICallbackAidl
import com.sumian.sd.IMusicServiceAidl
import com.sumian.sd.R
import com.sumian.sd.buz.notification.NotificationConst
import com.sumian.sd.common.h5.music.bean.MusicInfo
import java.io.IOException

class MusicService : Service() {

    private lateinit var mMediaPlayer: MediaPlayer
    private var mCallback: ICallbackAidl? = null
    private var mPath: String = ""
    private var mPlayStatus: Int = MusicInfo.STATUS_STOP

    companion object {
        const val EXTRA_PATH = "extra_path"
        private const val DEBUG = false
    }

    private val mIMusicServiceAidl: IMusicServiceAidl = object : IMusicServiceAidl.Stub() {
        override fun startMusic() {
            this@MusicService.startMusic()
        }

        override fun pauseMusic() {
            this@MusicService.pauseMusic()
        }

        override fun switchMusic(path: String) {
            this@MusicService.switchMusic(path)
        }

        override fun stopMusic() {
            this@MusicService.stopMusic()
        }

        override fun seekMusic(msec: Int, status: Int) {
            val pauseStatus = 1
            val continueStatus = 2
            when (status) {
                pauseStatus -> {
                    mMediaPlayer.pause()
                }
                continueStatus -> {
                    mMediaPlayer.start()
                }
            }
            mMediaPlayer.seekTo(msec)
        }

        override fun getMusicInfo(): MusicInfo {
            return MusicInfo(mPath, mPlayStatus, mMediaPlayer.duration, mMediaPlayer.currentPosition)
        }

        override fun setCallback(callback: ICallbackAidl?) {
            mCallback = callback
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(NotificationConst.H5_MUSIC_CHANNEL_ID,
                    NotificationConst.H5_MUSIC_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            Notification.Builder(this, NotificationConst.H5_MUSIC_CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        val notification: Notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name) + getString(R.string.music_playing_tip))
                .build()
        startForeground(11, notification)
        initMediaPlayer(intent.getStringExtra(EXTRA_PATH) ?: "")
        return mIMusicServiceAidl.asBinder()
    }

    private fun initMediaPlayer(path: String) {
        if (path.isEmpty()) {
            ToastHelper.show("音乐资源路径为空，无法播放")
            return
        }
        try {
            if (!this::mMediaPlayer.isInitialized) {
                mMediaPlayer = MediaPlayer()
            }
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.prepare()
            mMediaPlayer.setOnCompletionListener {
                try {
                    Log.i("MCJ", "onComplete")
                    changePlayStatus(MusicInfo.STATUS_STOP)
                    mCallback?.onComplete()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mMediaPlayer.setOnPreparedListener {
                mPath = path
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startMusic() {
        mMediaPlayer.start()
        changePlayStatus(MusicInfo.STATUS_START)
    }

    private fun pauseMusic() {
        mMediaPlayer.pause()
        changePlayStatus(MusicInfo.STATUS_PAUSE)
    }

    private fun switchMusic(path: String) {
        if (path == mPath) {
            mMediaPlayer.start()
            changePlayStatus(MusicInfo.STATUS_START)
            return
        }
        initMediaPlayer(path)
        if (DEBUG) {
            mMediaPlayer.seekTo((mMediaPlayer!!.duration * 0.9f).toInt())
        }
        mMediaPlayer!!.start()
        changePlayStatus(MusicInfo.STATUS_PAUSE)
    }

    private fun stopMusic() {
        mMediaPlayer.stop()
        mPath = ""
        changePlayStatus(MusicInfo.STATUS_STOP)
    }

    private fun changePlayStatus (status: Int) {
        mPlayStatus = status
        mCallback?.onPlayStatusChange(status)
    }
}
