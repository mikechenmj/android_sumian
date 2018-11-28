package com.sumian.common.player

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
object CommonAudioPlayer {
    private const val PROGRESS_CHANGE_CALL_DURATION = 100L
    private var mMediaPlayer: MediaPlayer? = null
    private var mProgressTimer: Timer? = null
    private var mStateListener: StateListener? = null
    private var mStartOnPrepared = false

    fun prepare(url: String, startOnPrepared: Boolean = false) {
        mStartOnPrepared = startOnPrepared
        if (mMediaPlayer != null) {
            release()
        }
        mMediaPlayer = MediaPlayer().apply {
            @Suppress("DEPRECATION")
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepareAsync() // might take long! (for buffering, etc)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener {
                if (mMediaPlayer?.isLooping != true) {
                    mStateListener?.onPlayStatusChange(false)
                    prepareAsync()
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
                    mStateListener?.onProgressChange(mMediaPlayer?.currentPosition
                            ?: 0, mMediaPlayer?.duration ?: 0)
                })
            }
        }, 0, PROGRESS_CHANGE_CALL_DURATION)
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
        if (!isPlaying()) {
            mMediaPlayer?.start()
        }
        mStateListener?.onPlayStatusChange(true)
    }

    fun pause() {
        if (isPlaying()) {
            mMediaPlayer?.pause()
        }
        mStateListener?.onPlayStatusChange(false)
    }

    fun release() {
        mProgressTimer?.cancel()
        mMediaPlayer?.release()
        mStateListener?.onPlayStatusChange(false)
    }

    fun seekTo(mSec: Int) {
        mMediaPlayer?.seekTo(mSec)
    }

    interface StateListener {
        fun onPrepared()
        fun onProgressChange(progress: Int, total: Int)
        fun onPlayStatusChange(isPlaying: Boolean)
    }

    fun setStateChangeListener(listener: StateListener) {
        mStateListener = listener
    }

    fun getDuration(): Int {
        return mMediaPlayer?.duration ?: 0
    }
}
