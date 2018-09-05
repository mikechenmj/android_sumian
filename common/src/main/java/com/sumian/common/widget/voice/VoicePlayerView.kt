package com.sumian.common.widget.voice

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.util.Log
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
        private var TAG = VoicePlayerView::class.java.simpleName.toString()

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
        if (v.tag == null || v.tag == false) {
            v.tag = true
            iv_play.setImageResource(R.drawable.shap_play)
            onVoiceViewListener?.doPlay()
        } else {
            v.tag = null
            iv_play.setImageResource(R.drawable.ic_voice_play)
            onVoiceViewListener?.doPause()
        }
    }

    fun invalid(url: String, duration: Int, progress: Int, status: Int): VoicePlayerView {
        this.status = status
        Log.d(TAG, String.format("progress %d / %d", progress, duration))
        iv_play.setImageResource(when (status) {
            PLAYING_STATUS -> {
                iv_play.tag = true
                R.drawable.ic_voice_pause
            }
            PREPARE_STATUS -> {
                iv_play.tag = true
                R.drawable.rotate_loading
            }
            IDLE_STATUS -> {
                iv_play.tag = false
                R.drawable.ic_voice_play
            }
            else -> {
                iv_play.tag = false
                R.drawable.ic_voice_play
            }
        })
        if (status == PREPARE_STATUS) {
            iv_play.tag = true
            val wrappedDrawable = DrawableCompat.wrap(iv_play.drawable)
            DrawableCompat.setTint(wrappedDrawable, resources.getColor(R.color.t2_color))
            iv_play.setImageDrawable(wrappedDrawable)
            //R.drawable.dialog_loading_animation
        }

        if (duration > progress) {
            tv_duration.text = formatDuration((duration - progress) / 1000)
        }
        sb_progress.progress = progress
        sb_progress.max = duration
        return this
    }

    private fun formatDuration(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
    }

    fun getProgress(): Int {
        return sb_progress.progress
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