package com.sumian.sd.diary.sleeprecord.widget

import android.content.Context
import android.graphics.*
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/14 10:27
 * desc   : heihgt 110dp
 * version: 1.0
 */
class SleepRecordDiagramView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val barHeight = 120f
    var width = 0f
    var height = 0f
    var t0 = 0L
    var t1 = 0L
    var t2 = 0L
    var t3 = 0L
    var nwc = 0    // night wake count
    var tnwd = 0L   // total night wake duration
    var tobd = 0L   // total on bad duration
    val DAY_IN_MILLIS = DateUtils.DAY_IN_MILLIS
    val barColorNotSleep = Color.parseColor("#52CCA3")
    val barColorSleep = ColorCompatUtil.getColor(context, R.color.b3_color)
    var wdr = 0f // wdr = width / tobd
    var lineWidth = context.resources.getDimension(R.dimen.space_1)
    var lineHeightShort = context.resources.getDimension(R.dimen.space_56)
    var lineHeightLong = context.resources.getDimension(R.dimen.space_80)
    val lineAndTextColor = ColorCompatUtil.getColor(context, R.color.t2_color)
    val textBottomMargin = context.resources.getDimension(R.dimen.space_10)
    val textSize = context.resources.getDimension(R.dimen.font_14)
    val nwkBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.record_icon_workschedule)
    val iconBottomMargin = context.resources.getDimension(R.dimen.space_5)
    val iconSize = context.resources.getDimension(R.dimen.space_15)

    init {
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL
        paint.textSize = textSize

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        calWidthDurationRatio()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        // draw bar
        drawRect(canvas, 0f, width, barColorNotSleep)
        if (nwc > 0) {
            val snwd = tnwd / nwc   // single night wake duration
            val ssd = (t2 - t1 - tnwd) / (nwc + 1) // single sleep duration
            var ssx: Float // sleep start x
            var sex: Float // sleep end x
            var isx: Float // icon start x
            for (i in 0..nwc) {
                ssx = durationToWidth(t1 - t0 + i * (ssd + snwd))
                sex = ssx + durationToWidth(ssd)
                drawRect(canvas, ssx, sex, barColorSleep)
                if (i != nwc) {
                    isx = sex + durationToWidth(snwd) / 2 - iconSize / 2
                    drawIcon(canvas, isx)
                }
            }
        } else {
            drawRect(canvas, getXByTime(t1), getXByTime(t2), barColorSleep)
        }

        // draw line
        drawLine(canvas, getXByTime(t0), true)
        drawLine(canvas, getXByTime(t1), false)
        drawLine(canvas, getXByTime(t2), false)
        drawLine(canvas, getXByTime(t3) - lineWidth, true)

        // draw text
        paint.color = lineAndTextColor
        drawText(canvas, "${timeToString(t0)} 睡觉", getXByTime(t0), true, false)
        drawText(canvas, "${timeToString(t1)} 睡着", getXByTime(t1), false, false)
        drawText(canvas, "${timeToString(t2)} 醒来", getXByTime(t2), false, true)
        drawText(canvas, "${timeToString(t3)} 起床", getXByTime(t3), true, true)
    }

    private fun getXByTime(time: Long): Float {
        return durationToWidth(time - t0)
    }

    private fun durationToWidth(duration: Long): Float {
        return duration * wdr
    }

    private fun drawIcon(canvas: Canvas, x: Float) {
        canvas.drawBitmap(nwkBitmap, x, height - barHeight - iconBottomMargin - iconSize, paint)
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, isHeight: Boolean, isRightToLeft: Boolean) {
        paint.color = lineAndTextColor
        paint.textAlign = if (isRightToLeft) Paint.Align.RIGHT else Paint.Align.LEFT
        val lineHeight = if (isHeight) lineHeightLong else lineHeightShort
        canvas.drawText(text, x, height - lineHeight - textBottomMargin, paint)
    }

    private fun drawLine(canvas: Canvas, x: Float, isHeight: Boolean) {
        paint.color = lineAndTextColor
        val lineHeight = if (isHeight) lineHeightLong else lineHeightShort
        canvas.drawRect(x, height, x + lineWidth, height - lineHeight, paint)
    }

    private fun drawRect(canvas: Canvas, startX: Float, endX: Float, color: Int) {
        paint.color = color
        canvas.drawRect(startX, height - barHeight, endX, height, paint) // draw bg
    }

    /**
     * t0 - t3 依次是
     */
    fun setData(t0: Long, t1: Long, t2: Long, t3: Long, nightWakeCount: Int, nightWakeTotalDuration: Long) {
        this.t0 = t0
        this.t1 = t1
        this.t2 = t2
        this.t3 = t3
        nwc = nightWakeCount
        tnwd = nightWakeTotalDuration
        calData()
        invalidate()
    }

    private fun calData() {
        if (t1 < t0) {
            t1 += DAY_IN_MILLIS
        }
        if (t2 < t0) {
            t2 += DAY_IN_MILLIS
        }
        if (t3 < t0) {
            t3 += DAY_IN_MILLIS
        }
        tobd = t3 - t0
        calWidthDurationRatio()
    }

    private fun calWidthDurationRatio() {
        wdr = width / tobd
    }

    private fun timeToString(time: Long): String? {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
        return simpleDateFormat.format(Date(time))
    }
}