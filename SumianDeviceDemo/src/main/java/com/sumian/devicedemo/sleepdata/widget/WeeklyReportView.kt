package com.sumian.devicedemo.sleepdata.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.devicedemo.R
import com.sumian.devicedemo.sleepdata.data.SleepDurationReport
import kotlinx.android.synthetic.main.lay_sleep_data_less.view.*
import kotlinx.android.synthetic.main.view_weekly_report.view.*

class WeeklyReportView(context: Context, attributeSet: AttributeSet) :
        FrameLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_weekly_report, this, true)
    }

    fun setData(item: SleepDurationReport) {
        week_sleep_histogram_view.addSleepData(item.sleeps)
        daily_sleep_avg_compare_view.setAvgDuration(item.avg_sleep_duration)
                .setCompareDuration(item.diff_avg_sleep_duration)
        daily_sleep_light_avg_compare_view.setAvgDuration(item.avg_light_duration)
                .setCompareDuration(item.diff_avg_light_duration)
        daily_sleep_deep_avg_compare_view.setAvgDuration(item.avg_deep_duration)
                .setCompareDuration(item.diff_avg_deep_duration)
        daily_sleep_awake_avg_compare_view.setAvgDuration(item.avg_awake_duration)
                .setCompareDuration(item.diff_avg_awake_duration)
        lay_sleep_data_less_container.setOnClickListener {
            SleepAdviceDialog(context).setAdvice(item.advice).show()
        }
    }

}