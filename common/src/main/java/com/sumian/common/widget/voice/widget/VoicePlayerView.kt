package com.sumian.common.widget.voice.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.sumian.common.R
import com.sumian.common.widget.voice.player.VoicePlayer
import kotlinx.android.synthetic.main.lay_voice_play_view.view.*
import java.util.*

/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc:  医生播放器 view
 */
class VoicePlayerView : LinearLayout, View.OnClickListener, VoicePlayer.onPlayStatusListener, SeekBar.OnSeekBarChangeListener, IVisible {

    companion object {
        private val TAG = VoicePlayerView::class.java.simpleName
    }

    private var mVoice: Voice = Voice()

    private var mCurrentPosition: Int = -1

    private var mVoicePlayer: VoicePlayer? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        gravity = Gravity.CENTER
        orientation = LinearLayout.HORIZONTAL
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_voice_play_view, this)
        iv_play.setOnClickListener(this)
        sb_progress.setOnSeekBarChangeListener(this)
    }

    override fun onClick(v: View) {
        if (v.tag == null) {
            v.tag = true
            iv_play.setImageResource(R.drawable.shap_play)
        } else {
            v.tag = null
            iv_play.setImageResource(R.drawable.ic_play_icon)
        }

        mVoicePlayer?.play(mVoice.voicePath, mCurrentPosition)
    }

    override fun onProgressCallback(duration: Int, progress: Int) {
        sb_progress.progress = progress
        tv_duration.text = formatDuration(duration - progress)
        Log.e(TAG, tv_duration.text.toString())
        sb_progress.max = duration
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mVoicePlayer?.pause()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mVoice.status == Voice.IDLE_STATUS) {
            mVoicePlayer?.play(mVoice.voicePath, mCurrentPosition)
        } else {
            mVoicePlayer?.seekTo(seekBar.progress)
        }
    }

    override fun play() {
        mVoice.status = Voice.PLAYING_STATUS
        iv_play.setImageResource(R.drawable.shap_play)
        tv_duration.text = formatDuration(0)
        sb_progress.progress = 0
        sb_progress.max = mVoicePlayer?.duration!!
    }

    override fun stop() {
        mVoice.status = Voice.IDLE_STATUS
        iv_play.setImageResource(R.drawable.ic_play_icon)
        tv_duration.text = formatDuration(mVoice.duration)
        sb_progress.progress = 0
        sb_progress.max = mVoicePlayer?.duration!!
    }

    fun invalid(voice: Voice): VoicePlayerView {
        this.mVoice = voice
        iv_play.setImageResource(if (voice.status == Voice.IDLE_STATUS) R.drawable.ic_play_icon else R.drawable.shap_play)
        tv_duration.text = formatDuration(voice.duration)
        sb_progress.progress = 0
        sb_progress.max = voice.duration

        this.mVoicePlayer = VoicePlayer.getInstance().setStatusListener(this)
        return this
    }

    fun setPosition(currentPosition: Int): VoicePlayerView {
        this.mCurrentPosition = currentPosition
        return this
    }

    private fun formatDuration(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }

    fun release() {
        mVoicePlayer?.release()
    }

}