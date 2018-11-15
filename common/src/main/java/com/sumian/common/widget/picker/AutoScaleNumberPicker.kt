package com.sumian.common.widget.picker

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import cn.carbswang.android.numberpickerview.library.NumberPickerView

class AutoScaleNumberPicker : NumberPickerView, NumberPickerView.OnValueChangeListenerInScrolling {

    companion object {

        private val TAG = AutoScaleNumberPicker::class.java.simpleName
    }

    private val declaredSelectTextSize by lazy {
        return@lazy NumberPickerView::class.java.getDeclaredField("mTextSizeSelected")
    }

    private val mNormalTextSize by lazy {
        declaredSelectTextSize.isAccessible = true
        val superTextSizeSelected: Int = declaredSelectTextSize.get(this) as Int
        //if (mNormalTextSize <= 0) {
        //     mNormalTextSize = superTextSizeSelected
        // }
        superTextSizeSelected

    }
    private val mNormalPaint: Paint by lazy {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        textPaint.textSize = mNormalTextSize.toFloat()
        return@lazy textPaint
    }

    private val mAutoMeasureThread by lazy {
        HandlerThread("autoMeasure thread")
    }

    private val mAutoMeasureHandler by lazy {
        Handler(mAutoMeasureThread.looper)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mAutoMeasureThread.start()
        setOnValueChangeListenerInScrolling(this)
    }

    override fun refreshByNewDisplayedValues(display: Array<out String>?) {
        super.refreshByNewDisplayedValues(display)
        postAutoMeasure()
    }

    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        postAutoMeasure()
    }

    private fun postAutoMeasure() {
        mAutoMeasureHandler.post {
            if (mNormalPaint.textSize <= mNormalTextSize) {
                mNormalPaint.textSize = mNormalTextSize.toFloat()
            }
            var autoMeasureTextSize = autoMeasureTextSize(mNormalPaint, mNormalTextSize.toFloat(), width.toFloat(), contentByCurrValue)
            if (autoMeasureTextSize >= mNormalTextSize) {
                autoMeasureTextSize = mNormalTextSize
            }
            declaredSelectTextSize.isAccessible = true
            declaredSelectTextSize.set(this, autoMeasureTextSize)
            postInvalidateOnAnimation()
        }
    }

    private fun autoMeasureTextSize(textPaint: Paint, textSize: Float, totalWidth: Float, content: String): Int {
        val measureTextWidth = measureText(textPaint, content)
        //Log.e(TAG, "PaintTextSize=${textPaint.textSize} textSize=$textSize  measureTextWidth=$measureTextWidth  totalWidth=$totalWidth contentByCurrValue=$contentByCurrValue")
        return if (measureTextWidth >= totalWidth) {//超过了控件大小，需要进行缩放
            textPaint.textSize = (textSize - 1)
            autoMeasureTextSize(textPaint, textPaint.textSize, totalWidth, content)
        } else {//小于控件大小，直接返回
            textSize.toInt()
        }
    }

    private fun measureText(textPaint: Paint, content: String): Float = textPaint.measureText(content) + 0.5f

}