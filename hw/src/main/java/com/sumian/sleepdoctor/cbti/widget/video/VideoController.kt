package com.sumian.sleepdoctor.cbti.widget.video

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_video_controller_view.view.*
import java.util.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc: CBTI  视频播放器控制器
 */
class VideoController : LinearLayout, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private var onControllerCallback: OnControllerCallback? = null

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        onControllerCallback?.pause()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        onControllerCallback?.play()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_player_status -> {
                if (v.tag == null) {
                    v.tag = true
                    iv_player_status.setImageResource(R.drawable.ic_cbti_icon_pause_s)
                    onControllerCallback?.play()
                } else {
                    v.tag = null
                    iv_player_status.setImageResource(R.drawable.ic_cbti_icon_play_s)
                    onControllerCallback?.pause()
                }
            }
            R.id.tv_lesson_list -> {
                onControllerCallback?.showContent()
            }
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_video_controller_view, this)

        iv_player_status.setOnClickListener(this)
        sb_progress.setOnSeekBarChangeListener(this)
    }

    fun setOnControllCallback(onControllerCallback: OnControllerCallback) {
        this.onControllerCallback = onControllerCallback
    }

    fun updateDuration(currentPosition: Int, duration: Int) {
        val currentMin = currentPosition / 60
        val currentSecond = currentPosition % 60

        val min = duration / 60
        val second = duration % 60
        tv_duration.text = String.format(Locale.getDefault(), "%02d:%02d/%02d:%02d", currentMin, currentSecond, min, second)

        val progress = duration / 100 * currentPosition

        sb_progress.progress = progress
    }

    fun setFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            tv_lesson_list.visibility = View.VISIBLE
            iv_scale.visibility = View.GONE
        } else {
            iv_scale.visibility = View.VISIBLE
            tv_lesson_list.visibility = View.GONE
        }
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    interface OnControllerCallback {

        fun showContent()

        fun play()

        fun pause()

    }
}