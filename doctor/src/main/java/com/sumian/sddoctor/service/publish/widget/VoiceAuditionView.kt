@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.service.publish.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.widget.voice.IVisible
import com.sumian.common.widget.voice.VoicePlayerView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.util.ResUtils
import kotlinx.android.synthetic.main.lay_record_audition_view.view.*

/**
 * Created by dq
 *
 * on 2018/8/31
 *
 * desc: 音频试听 widget
 */
class VoiceAuditionView : LinearLayout, View.OnClickListener, IVisible {

    private var voiceAuditionCallback: VoiceAuditionCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = VERTICAL
        setBackgroundColor(ResUtils.getColor(R.color.b2_color))
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_record_audition_view, this)

        btn_send.text = QMUISpanHelper.generateSideIconText(true, resources.getDimensionPixelSize(R.dimen.dp_10), "发送", resources.getDrawable(R.drawable.ic_diary_send))
        btn_send.setOnClickListener(this)

        btn_re_record.text = QMUISpanHelper.generateSideIconText(true, resources.getDimensionPixelSize(R.dimen.dp_10), "重录", resources.getDrawable(R.drawable.ic_diary_rerecord))
        btn_re_record.setOnClickListener(this)

    }

    fun setVoice(url: String, duration: Int, progress: Int, isRollback: Boolean, status: Int) {
        voice_player_view.invalid(url, duration, isRollback, progress, status).show()
    }

    fun setOnVoiceViewListener(onVoiceViewListener: VoicePlayerView.OnVoiceViewListener) {
        voice_player_view.setOnVoiceViewListener(onVoiceViewListener)
    }

    fun setVoiceAuditionCallback(voiceAuditionCallback: VoiceAuditionCallback) {
        this.voiceAuditionCallback = voiceAuditionCallback
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_send -> {
                voiceAuditionCallback?.publishVoice()
            }
            R.id.btn_re_record -> {
                voiceAuditionCallback?.reRecordVoice()
            }
        }
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }


    interface VoiceAuditionCallback {

        fun publishVoice()

        fun reRecordVoice()
    }
}