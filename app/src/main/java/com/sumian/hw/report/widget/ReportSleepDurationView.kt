package com.sumian.hw.report.widget

import android.content.Context
import android.util.AttributeSet
import com.sumian.hw.widget.base.BaseBlueCardView
import com.sumian.sd.R
import kotlinx.android.synthetic.main.hw_lay_sleep_duration_view.view.*

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

class ReportSleepDurationView : BaseBlueCardView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun getLayoutRes(): Int {
        return R.layout.hw_lay_sleep_duration_view
    }

    fun setSleepTodayDuration(sleepTodayDuration: Int) {
        tv_sleep_today_duration.setDuration(sleepTodayDuration)
    }

    fun setDeepSleepData(deepSleepDuration: Int, deepSleepDurationPercent: Int) {
        deep_sleep_duration_view.setData(resources.getString(R.string.deep_sleep_duration_hint), deepSleepDuration, deepSleepDurationPercent)
    }

    fun setLightSleepData(lightSleepDuration: Int, lightSleepDurationPercent: Int) {
        light_sleep_duration_view.setData(resources.getString(R.string.light_sleep_duration_hint), lightSleepDuration, lightSleepDurationPercent)
    }

}
