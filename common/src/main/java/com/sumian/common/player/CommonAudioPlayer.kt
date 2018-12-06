@file:Suppress("DEPRECATION")

package com.sumian.common.player

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import com.sumian.common.utils.SumianExecutor
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/27 16:48
 * desc   :
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object CommonAudioPlayer {
    private const val PROGRESS_CHANGE_CALL_DURATION = 100L
    private var mMediaPlayer: MediaPlayer? = null
    private var mProgressTimer: Timer? = null
    private var mStateListener: StateListener? = null
    private var mStartOnPrepared = false
    private var mContext: Context? = null
    private var mAudioManager: AudioManager? = null
    private var mAutoPlayIfPossible = false

    fun prepare(context: Context, url: String, startOnPrepared: Boolean = false) {
        mContext = context.applicationContext
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mStartOnPrepared = startOnPrepared
        if (mMediaPlayer != null) {
            release()
        }
        mMediaPlayer = MediaPlayer().apply {
            @Suppress("DEPRECATION")
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepareAsync() // might take long! (for buffering, etc)
            mStateListener?.onPreparing()
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener {
                if (mMediaPlayer?.isLooping != true) {
                    mStateListener?.onPlayStatusChange(false)
                    prepareAsync()
                    mStateListener?.onPreparing()
                }
            }
            isLooping = true
        }
    }

    private fun onPrepared() {
        startProgressTimer()
        if (mStartOnPrepared) {
            play()
        }
        mStateListener?.onPrepared()
        updateProgress()
    }

    private fun startProgressTimer() {
        mProgressTimer?.cancel()
        mProgressTimer = Timer()
        mProgressTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (mMediaPlayer == null || !mMediaPlayer!!.isPlaying) {
                    return
                }
                SumianExecutor.runOnUiThread({
                    updateProgress()
                })
            }
        }, 0, PROGRESS_CHANGE_CALL_DURATION)
    }

    private fun updateProgress() {
        mStateListener?.onProgressChange(mMediaPlayer?.currentPosition
                ?: 0, mMediaPlayer?.duration ?: 0)
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying == true
    }

    fun playOrPause() {
        if (isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun play() {
        if (isPlaying()) {
            return
        }
        val requestAudioFocus = mAudioManager?.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (requestAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer?.start()
            mStateListener?.onPlayStatusChange(true)
            registerBecomingNoisyReceiver()
            mAutoPlayIfPossible = false
        }
    }

    fun pause(autoPlayWhenPossible: Boolean = false) {
        if (!isPlaying()) {
            return
        }
        mMediaPlayer?.pause()
        mStateListener?.onPlayStatusChange(false)
        unregisterBecomingNoisyReceiver()
        if (!autoPlayWhenPossible) {
            mAudioManager?.abandonAudioFocus(mAudioFocusChangeListener)
        }
        mAutoPlayIfPossible = autoPlayWhenPossible
    }

    fun release() {
        mContext = null
        mProgressTimer?.cancel()
        mMediaPlayer?.release()
        mStateListener?.onPlayStatusChange(false)
        mAudioManager?.abandonAudioFocus(mAudioFocusChangeListener)
    }

    fun seekTo(mSec: Int) {
        mMediaPlayer?.seekTo(mSec)
    }

    fun setStateChangeListener(listener: StateListener) {
        mStateListener = listener
    }

    fun getDuration(): Int {
        return mMediaPlayer?.duration ?: 0
    }

    private fun registerBecomingNoisyReceiver() {
        mContext?.registerReceiver(mBecomingNoisyReceiver, mBecomingNoiseIntentFilter)
    }

    private fun unregisterBecomingNoisyReceiver() {
        mContext?.unregisterReceiver(mBecomingNoisyReceiver)
    }

    private val mBecomingNoiseIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    /**
     * 处理耳机拔出的场景
     */
    private var mBecomingNoisyReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                pause()
            }
        }
    }

    /**
     * 处理AudioChange的场景，如接电话
     */
    private val mAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pause(true)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mAutoPlayIfPossible) {
                    play()
                }
            }
        }
    }

    interface StateListener {
        fun onPreparing()
        fun onPrepared()
        fun onProgressChange(progress: Int, total: Int)
        fun onPlayStatusChange(isPlaying: Boolean)
    }
}

