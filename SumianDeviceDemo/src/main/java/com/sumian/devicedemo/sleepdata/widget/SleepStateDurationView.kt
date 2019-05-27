package com.sumian.sd.buz.report.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sumian.devicedemo.R
import kotlinx.android.synthetic.main.hw_lay_sleep_state_duration_view.view.*

/**
 * Created by sm
 * on 2018/3/8.
 * desc:睡眠报告中,睡眠时长统计容器
 */

class SleepStateDurationView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.hw_lay_sleep_state_duration_view, this)
    }

    fun setData(label: String, duration: Int, percent: Int) {
        tv_label.text = label
        tv_sleep_duration_count.setDuration(duration)
        tv_sleep_duration_percent.setPercent(percent)
    }
}
