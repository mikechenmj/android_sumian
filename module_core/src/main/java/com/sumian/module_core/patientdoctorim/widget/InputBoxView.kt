package com.sumian.module_core.patientdoctorim.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.module_core.R
import kotlinx.android.synthetic.main.core_im_input_box.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:37
 * desc   :
 * version: 1.0
 */
class InputBoxView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    private var mTimer: CountDownTimer? = null
    private val mIsRecording = false
    private var mRecordTime = 0
    private var mHost: Host? = null

    companion object {
        private const val MIN_RECORD_TIME = 1
        private const val MAX_RECORD_TIME = 3
    }

    init {
        View.inflate(context, R.layout.core_im_input_box, this)
        initRecordPanel()
        iv_switch_voice.setOnClickListener { showRecordVoicePanel(!iv_switch_voice.isSelected) }
        bt_send.setOnClickListener { sendMessage() }
        et_message.addTextChangedListener(object : EmptyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                bt_send.isEnabled = !TextUtils.isEmpty(s)
            }
        })
    }

    private fun sendMessage() {
        mHost?.sendMessage(et_message.text.toString(), object : SendMessageCallback {
            override fun onSuccess() {
                et_message.text = null
            }

            override fun onFail(code: Int, message: String) {
                ToastUtils.showShort(message)
            }
        })
    }

    private fun initRecordPanel() {
        record_voice_container.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val action = event.action
                LogUtils.d("container - $action")
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val isInCancelView = isTouchPointInView(iv_cancel_record, x, y)
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (isInRecordView(x, y)) {
                            startRecord()
                            return true
                        } else {
                            return false
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        LogUtils.d("isInCancelView $isInCancelView")
                        tv_record_voice_hint.text = resources.getString(if (isInCancelView) R.string.release_finger_cancel_send else R.string.move_right_cancel_send)
                        tv_record_voice_hint.setTextColor(resources.getColor(if (isInCancelView) R.color.t4_color else R.color.t2_color))
                        iv_cancel_record.isSelected = isInCancelView
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecord(isInCancelView)
                    }
                }
                return true
            }
        })
    }

    private fun isInRecordView(x: Int, y: Int) = isTouchPointInView(iv_record_voice, x, y)

    private fun startRecord() {
        iv_cancel_record.isSelected = false
        tv_record_voice_warning.isVisible = false
        tv_record_voice_hint.isVisible = true
        tv_record_voice_hint.text = resources.getString(R.string.move_right_cancel_send)
        tv_record_voice_hint.setTextColor(resources.getColor(R.color.t2_color))
        startTimer()
        mRecordTime = 0
    }

    private fun stopRecord(cancel: Boolean) {
        mTimer?.cancel()
        tv_record_voice_hint.isVisible = false
        if (cancel) {
            LogUtils.d("cancel record")
        } else {
            LogUtils.d("finish record")
            if (mRecordTime < MIN_RECORD_TIME) {
                showWarningHint(true)
            } else if (mRecordTime == MAX_RECORD_TIME) {
                showWarningHint(false)
            } else {
                sendVoice()
            }
        }
    }

    private fun sendVoice() {
        LogUtils.d("send voice")
        mHost?.sendVoice("file path", object : SendMessageCallback {
            override fun onSuccess() {

            }

            override fun onFail(code: Int, message: String) {
                ToastUtils.showShort(message)
            }
        })
    }

    private fun startTimer() {
        mTimer = object : CountDownTimer(1000L * MAX_RECORD_TIME, 1000L) {
            override fun onFinish() {
                stopRecord(false)
            }

            override fun onTick(millisUntilFinished: Long) {
                mRecordTime++
            }
        }.start()
    }

    private fun showWarningHint(isTooShort: Boolean) {
        val hintText = resources.getString(if (isTooShort) R.string.record_too_short else R.string.record_too_long)
        val span = QMUISpanHelper.generateSideIconText(true,
                resources.getDimensionPixelOffset(R.dimen.space_5),
                hintText, resources.getDrawable(R.drawable.inputbox_icon_caveat))
        tv_record_voice_warning.text = span
        tv_record_voice_warning.isVisible = true
        tv_record_voice_warning.postDelayed({ tv_record_voice_warning.isVisible = false }, 2000)
    }

    private fun isTouchPointInView(view: View, x: Int, y: Int): Boolean {
        val location = intArrayOf(0, 0)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.width
        val bottom = top + view.height
        return x in left..right && y in top..bottom
    }

    fun showRecordVoicePanel(show: Boolean) {
        iv_switch_voice.isSelected = show
        record_voice_container.isVisible = show
        if (show) {
            KeyboardUtils.hideSoftInput(this)
        }
    }

    interface Host {
        fun sendMessage(message: String, sendMessageCallback: SendMessageCallback)
        fun sendVoice(file: String, sendMessageCallback: SendMessageCallback)
    }

    interface SendMessageCallback {
        fun onSuccess()
        fun onFail(code: Int, message: String)
    }
}