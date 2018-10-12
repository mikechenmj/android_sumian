package com.sumian.sd.device.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 20:51
 * desc   :
 * version: 1.0
 */
class BatteryProgressView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {
    private var mProgress = 0
    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    fun setProgress(progress: Int) {
        mProgress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = getBgColor()
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 30.toFloat(), 30.toFloat(), mPaint)
        canvas.clipRect(0f, 0f, width.toFloat() * mProgress / 100, height.toFloat())
        mPaint.color = getProgressColor()
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 30.toFloat(), 30.toFloat(), mPaint)
    }

    private fun getBgColor(): Int {
        return ColorCompatUtil.getColor(context, R.color.l3_color)
    }

    private fun getProgressColor(): Int {
        return ColorCompatUtil.getColor(context, if (mProgress > 20) R.color.b3_color else R.color.t4_color)
    }
}