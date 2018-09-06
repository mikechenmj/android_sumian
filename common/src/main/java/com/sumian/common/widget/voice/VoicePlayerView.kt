package com.sumian.common.widget.voice

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.sumian.common.R
import com.sumian.common.widget.adapter.SeekBarAdapter
import kotlinx.android.synthetic.main.lay_voice_play_view.view.*
import java.util.*


@Suppress("DEPRECATION")
/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc:  音频播放 widget
 */
class VoicePlayerView : LinearLayout, View.OnClickListener, IVisible {

    companion object {

        private const val IDLE_STATUS = 0x00
        private const val PREPARE_STATUS = 0x01
        private const val PLAYING_STATUS = 0x02

    }


    private var onVoiceViewListener: OnVoiceViewListener? = null

    private var status = IDLE_STATUS

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        gravity = Gravity.CENTER
        orientation = LinearLayout.HORIZONTAL
        initView(context)
    }

    fun setOnVoiceViewListener(onVoiceViewListener: OnVoiceViewListener): VoicePlayerView {
        this.onVoiceViewListener = onVoiceViewListener
        return this
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_voice_play_view, this)
        iv_play.setOnClickListener(this)
        sb_progress.setOnSeekBarChangeListener(object : SeekBarAdapter() {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                super.onStopTrackingTouch(seekBar)
                if (status == IDLE_STATUS) {
                    onVoiceViewListener?.doPlay()
                } else {
                    onVoiceViewListener?.doSeekTo(seekBar.progress)
                }
            }
        })
    }

    override fun onClick(v: View) {
        if (v.tag == null) {
            v.tag = true
            iv_play.setImageResource(R.drawable.ic_voice_hear_suspend)
            onVoiceViewListener?.doPlay()
        } else {
            v.tag = null
            iv_play.setImageResource(R.drawable.ic_voice_hear_play)
            onVoiceViewListener?.doPause()
        }
    }

    fun invalid(url: String, duration: Int, progress: Int, status: Int): VoicePlayerView {
        this.status = status
        iv_play.setImageResource(when (status) {
            IDLE_STATUS -> {
                iv_play.tag = null
                R.drawable.ic_voice_hear_play
            }
            PREPARE_STATUS -> {
                iv_play.tag = true
                R.drawable.play_loading_animation
            }
            PLAYING_STATUS -> {
                iv_play.tag = true
                R.drawable.ic_voice_hear_suspend
            }
            else -> {
                iv_play.tag = null
                R.drawable.ic_voice_hear_play
            }
        })

        //为什么这么做,因为 mediaPlayer  默认是毫秒级   但是服务器 返回的是 s.如果用 ms 那么动画进度看着会细腻点.所以需要特殊处理一下
        val tmpDuration: Int = if (duration < 1000) {
            duration * 1000
        } else {
            duration
        }

        tv_duration.text = formatDuration((tmpDuration - progress) / 1000)

        sb_progress.progress = progress
        sb_progress.max = tmpDuration
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

    interface OnVoiceViewListener {

        fun doPlay()

        fun doPause()

        fun doSeekTo(position: Int)
    }


}