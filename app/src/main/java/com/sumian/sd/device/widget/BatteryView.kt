package com.sumian.sd.device.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_battery.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 21:18
 * desc   :
 * version: 1.0
 */
class BatteryView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_battery, this, true)
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        battery_progress_view.setProgress(progress)
        tv_progress.text = if (progress == 0) "——" else "$progress%"
    }

    fun setTextSize(size: Float) {
        tv_progress.setTextSize(TypedValue.COMPLEX_UNIT_SP,size)
    }
}