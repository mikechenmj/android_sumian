package com.sumian.sddoctor.service.publish.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.util.ResUtils

@Suppress("DEPRECATION")
/**
 * Created by sm
 *
 * on 2018/9/4
 *
 * desc:录音进度 view
 *
 */
class VoiceTextView : TextView, Runnable {

    private val oneRadius by lazy {
        resources.getDimension(R.dimen.space_150) / 2.0f
    }

    private val twoRadius by lazy {
        resources.getDimension(R.dimen.space_200) / 2.0f
    }
    private val threeRadius by lazy {
        resources.getDimension(R.dimen.space_250) / 2.0f
    }

    private var centerX = 0.0f
    private var centerY = 0.0f

    private var progress = 0.0f

    private var mMaxRadius: Int = 0

    private val interval = 100
    private var count = 0

    private val mProgressOval: RectF  by lazy {
        RectF()
    }

    private var onePaint: Paint? = null

    private var twoPaint: Paint? = null

    private var threePaint: Paint? = null

    private var progressPaint: Paint? = null

    private var ripplePaint: Paint? = null

    private var isRipple = false


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initPaint()
    }


    private fun initPaint() {

        onePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        onePaint?.color = resources.getColor(R.color.l1_color)
        onePaint?.style = Paint.Style.STROKE
        onePaint?.strokeWidth = resources.getDimension(R.dimen.space_2)

        twoPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        //twoPaint.color = resources.getColor(R.color.l1_66_color)
        twoPaint?.color = Color.TRANSPARENT
        twoPaint?.style = Paint.Style.STROKE
        twoPaint?.strokeWidth = resources.getDimension(R.dimen.space_1)

        threePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        // threePaint.color = resources.getColor(R.color.l1_33_color)
        threePaint?.color = Color.TRANSPARENT
        threePaint?.style = Paint.Style.STROKE
        threePaint?.strokeWidth = resources.getDimension(R.dimen.space_1)

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        progressPaint?.color = resources.getColor(R.color.b3_color)
        progressPaint?.style = Paint.Style.STROKE
        progressPaint?.strokeCap = Paint.Cap.ROUND
        progressPaint?.strokeJoin = Paint.Join.ROUND
        progressPaint?.strokeWidth = resources.getDimension(R.dimen.space_2)

        ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        ripplePaint?.color = resources.getColor(R.color.l1_color)
        ripplePaint?.style = Paint.Style.STROKE
        ripplePaint?.strokeWidth = resources.getDimension(R.dimen.space_1)

    }

    override fun onDraw(canvas: Canvas) {
        //绘制ripple背景
        if (isRipple) {
            val save = canvas.save()
            var step = count
            while (step < mMaxRadius) {
                ripplePaint?.alpha = (255 * (mMaxRadius - step) / mMaxRadius)
                canvas.drawCircle(centerX, centerY, (when {
                    step >= mMaxRadius -> (mMaxRadius - 3).toFloat()
                    step + oneRadius <= mMaxRadius -> step + oneRadius
                    else -> mMaxRadius.toFloat()
                }), ripplePaint!!)
                step += interval
            }
            canvas.restoreToCount(save)

            postDelayed(this, 0)
        }

        canvas.drawCircle(centerX, centerY, oneRadius, onePaint!!)
        //canvas.drawCircle(centerX, centerY, twoRadius, twoPaint!!)
        // canvas.drawCircle(centerX, centerY, threeRadius, threePaint!!)

        canvas.drawArc(mProgressOval, -90.0f, progress, false, progressPaint!!)

        super.onDraw(canvas)

    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (TextUtils.isEmpty(text) || text!! == "0:00") {
            setTextColor(ResUtils.getColor(R.color.t2_color))
            progress = 0.0f
            count = 0
            //twoPaint?.color = Color.TRANSPARENT
            //threePaint?.color = Color.TRANSPARENT
            isRipple = false
        } else {
            setTextColor(ResUtils.getColor(R.color.b3_color))
            progress += 360.0f / 180.0f
            //twoPaint?.color = resources.getColor(R.color.l1_66_color)
            //threePaint?.color = resources.getColor(R.color.l1_33_color)
            isRipple = true
        }
        super.setText(text, type)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = (w - paddingLeft - paddingRight).shr(1).toFloat()
        centerY = (h - paddingTop - paddingBottom).shr(1).toFloat()

        this.mProgressOval.set(centerX - oneRadius, centerY - oneRadius, centerX + oneRadius, centerY + oneRadius)

        mMaxRadius = Math.min(w, h) shr 1
    }

    override fun run() {
        count += 2
        count %= interval
        postInvalidateOnAnimation()
    }

}