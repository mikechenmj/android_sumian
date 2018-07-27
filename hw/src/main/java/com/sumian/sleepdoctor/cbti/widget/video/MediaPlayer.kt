package com.sumian.sleepdoctor.cbti.widget.video

import android.content.Context
import android.view.Surface
import com.aliyun.vodplayer.media.AliyunVodPlayer
import com.aliyun.vodplayer.media.IAliyunVodPlayer

/**
 * Created by dq
 *
 * on 2018/7/18
 *
 * desc:  aliyun 多媒体播放器
 */
class MediaPlayer private constructor(context: Context) : IAliyunVodPlayer.OnPreparedListener, IAliyunVodPlayer.OnFirstFrameStartListener, IAliyunVodPlayer.OnErrorListener, IAliyunVodPlayer.OnCompletionListener, IAliyunVodPlayer.OnSeekCompleteListener, IAliyunVodPlayer.OnStoppedListener, IAliyunVodPlayer.OnUrlTimeExpiredListener {

    private var mCurrentPostion = 0

    private val mMediaPlayer: AliyunVodPlayer by lazy {

        val aliyunVodPlayer = AliyunVodPlayer(context)

        aliyunVodPlayer.enableNativeLog()

        aliyunVodPlayer.setOnPreparedListener(this)
        aliyunVodPlayer.setOnFirstFrameStartListener(this)
        aliyunVodPlayer.setOnErrorListener(this)
        aliyunVodPlayer.setOnCompletionListener(this)
        aliyunVodPlayer.setOnSeekCompleteListener(this)
        aliyunVodPlayer.setOnStoppedListner(this)
        // aliyunVodPlayer.setOnChangeQualityListener(this)
        aliyunVodPlayer.setOnUrlTimeExpiredListener(this)

        return@lazy aliyunVodPlayer
    }

    init {


    }

    companion object {
        fun create(context: Context): MediaPlayer {
            return MediaPlayer(context)
        }
    }

    override fun onUrlTimeExpired(p0: String?, p1: String?) {

    }

    override fun onStopped() {

    }

    override fun onSeekComplete() {

    }

    override fun onCompletion() {

    }

    override fun onError(p0: Int, p1: Int, p2: String?) {

    }

    override fun onFirstFrameStart() {

    }

    override fun onPrepared() {

    }


    fun play() {
        mMediaPlayer.start()
    }

    fun setSurface(surface: Surface) {
        mMediaPlayer.setSurface(surface)
    }
}