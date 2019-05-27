package com.sumian.devicedemo.sleepdata.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.sumian.devicedemo.R
import kotlinx.android.synthetic.main.hw_lay_sleep_duration_view.view.*

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

class ReportSleepDurationView : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.hw_lay_sleep_duration_view, this)
    }

    fun setSleepTodayDuration(sleepTodayDuration: Int) {
        tv_sleep_today_duration.setDuration(sleepTodayDuration)
    }

    fun setDeepSleepData(deepSleepDuration: Int, deepSleepDurationPercent: Int) {
        deep_sleep_duration_view.setData(
                resources.getString(R.string.deep_sleep_duration_hint),
                deepSleepDuration,
                deepSleepDurationPercent
        )
    }

    fun setLightSleepData(lightSleepDuration: Int, lightSleepDurationPercent: Int) {
        light_sleep_duration_view.setData(
                resources.getString(R.string.light_sleep_duration_hint),
                lightSleepDuration,
                lightSleepDurationPercent
        )
    }

}
