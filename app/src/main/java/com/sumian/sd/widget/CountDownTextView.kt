package com.sumian.sd.widget

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.widget.Button
import com.sumian.sd.R

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/17 15:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CountDownTextView(context: Context, attrs: AttributeSet?) : Button(context, attrs) {
    private val mCountDownTimer: CountDownTimer

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView)
        val normalText = attributes.getString(R.styleable.CountDownTextView_cdtv_normal_text)
        val countDownTextId = attributes.getResourceId(R.styleable.CountDownTextView_cdtv_count_down_text, 0)
        val countDownTimeInSeconds = attributes.getInteger(R.styleable.CountDownTextView_cdtv_count_down_time, 60)
        attributes.recycle()
        text = normalText
        mCountDownTimer = object : CountDownTimer(1000L * countDownTimeInSeconds, 1000) {
            override fun onFinish() {
                text = normalText
                isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                text = context.getString(countDownTextId, millisUntilFinished / 1000)
            }
        }
    }

    fun startCountDown() {
        isEnabled = false
        mCountDownTimer.start()
    }

    override fun onDetachedFromWindow() {
        mCountDownTimer.cancel()
        super.onDetachedFromWindow()
    }
}