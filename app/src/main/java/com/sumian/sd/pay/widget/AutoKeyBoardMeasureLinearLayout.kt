package com.sumian.sd.pay.widget

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.blankj.utilcode.util.ActivityUtils


class AutoKeyBoardMeasureLinearLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mRootView: View? = null

    init {
        ActivityUtils.getTopActivity()?.let {
            mRootView = it.window.decorView
        }
    }

    companion object {
        private val TAG = AutoKeyBoardMeasureLinearLayout::class.java.simpleName
    }

    private var mDisplayHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        var height = 0
        //when (mode) {
        //   //MeasureSpec.EXACTLY -> {
        height = MeasureSpec.getSize(heightMeasureSpec)
        // }
        //}

        // Get DisplayHeight
        val metrics = DisplayMetrics()
        val display = ActivityUtils.getTopActivity().windowManager.defaultDisplay.getMetrics(metrics)
        //val point = Point()
        // display.getSize(point)
        mDisplayHeight = metrics.heightPixels
        // mDisplayHeight = point.y

        val frame = Rect()
        mRootView?.getWindowVisibleDisplayFrame(frame)

        Log.e(TAG, "onMeasure: -------->height=$height  mDisplayHeight=$mDisplayHeight  frameHeight=${frame.height()}")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val active = inputMethodManager.isActive(this)
        Log.e("TAG", "onLayout:  changed=$changed  top=$top  bottom=$bottom  active=$active   ${inputMethodManager.isActive}")

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e(TAG, "onSizeChanged: --------->h=$h  oldh=$oldh")
    }
}