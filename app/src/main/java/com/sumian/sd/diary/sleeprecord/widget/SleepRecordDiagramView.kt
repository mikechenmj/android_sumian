package com.sumian.sd.diary.sleeprecord.widget

import android.content.Context
import android.graphics.*
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.TimeUtilV2
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
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barHeight = 120f
    private var width = 0f
    private var height = 0f
    private var t0 = 0L
    private var t1 = 0L
    private var t2 = 0L
    private var t3 = 0L
    private var nwc = 0    // night wake count
    private var tnwd = 0L   // total night wake duration
    private var tobd = 0L   // total on bad duration
    private val barColorNotSleep = Color.parseColor("#52CCA3")
    private val barColorSleep = ColorCompatUtil.getColor(context, R.color.b3_color)
    private var wdr = 0f // wdr = width / tobd
    private var lineWidth = context.resources.getDimension(R.dimen.space_1)
    private var lineHeightShort = context.resources.getDimension(R.dimen.space_56)
    private var lineHeightLong = context.resources.getDimension(R.dimen.space_80)
    private val textColor = ColorCompatUtil.getColor(context, R.color.t2_color)
    private val lineColor = ColorCompatUtil.getColor(context, R.color.l3_color)
    private val textBottomMargin = context.resources.getDimension(R.dimen.space_10)
    private val textSize = context.resources.getDimension(R.dimen.font_14)
    private val nwkBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.record_icon_workschedule)
    private val iconBottomMargin = context.resources.getDimension(R.dimen.space_5)
    private val dayThresholdTime = TimeUtilV2.parseTimeStr("18:00")

    val iconSize = context.resources.getDimension(R.dimen.space_15)

    companion object {
        private const val DAY_IN_MILLIS = DateUtils.DAY_IN_MILLIS
        private const val HOUR_IN_MILLIS = DateUtils.HOUR_IN_MILLIS
    }

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
        paint.color = textColor
        paint.textAlign = if (isRightToLeft) Paint.Align.RIGHT else Paint.Align.LEFT
        val lineHeight = if (isHeight) lineHeightLong else lineHeightShort
        canvas.drawText(text, x, height - lineHeight - textBottomMargin, paint)
    }

    private fun drawLine(canvas: Canvas, x: Float, isHeight: Boolean) {
        paint.color = lineColor
        val lineHeight = if (isHeight) lineHeightLong else lineHeightShort
        canvas.drawRect(x, height, x + lineWidth, height - lineHeight, paint)
    }

    private fun drawRect(canvas: Canvas, startX: Float, endX: Float, color: Int) {
        paint.color = color
        canvas.drawRect(startX, height - barHeight, endX, height, paint) // draw bg
    }

    /**
     * t0 - t3 依次是 上床时间，睡着时间，醒来时间，起床时间
     * 上床时间 睡着时间 18:00~23:55 算昨天 00:00~17:55(18:00) 算今天
     * 醒来时间 起床时间 永远是算今天
     *
     */
    fun setData(t0: Long, t1: Long, t2: Long, t3: Long, nightWakeCount: Int, nightWakeTotalDuration: Long) {
        val isBedAtToday = t0 < dayThresholdTime
        this.t0 = t0 + if (isBedAtToday) DAY_IN_MILLIS else 0
        this.t1 = t1 + if (isBedAtToday || t1 < dayThresholdTime) DAY_IN_MILLIS else 0
        this.t2 = t2 + DAY_IN_MILLIS
        this.t3 = t3 + DAY_IN_MILLIS
        tobd = this.t3 - this.t0
        calWidthDurationRatio()

        nwc = nightWakeCount
        tnwd = nightWakeTotalDuration
        invalidate()
    }

    private fun calWidthDurationRatio() {
        wdr = width / tobd
    }

    private fun timeToString(time: Long): String? {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
        return simpleDateFormat.format(Date(time))
    }
}