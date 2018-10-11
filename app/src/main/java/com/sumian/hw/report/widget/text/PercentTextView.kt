package com.sumian.hw.report.widget.text

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.widget.ColorfulProgressView
import kotlinx.android.synthetic.main.lay_sleep_percent_view.view.*

/**
 * Created by sm
 *
 * on 2018/9/19
 *
 * desc:
 *
 */
class PercentTextView : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        View.inflate(context, R.layout.lay_sleep_percent_view, this)
    }

    fun setPercent(percent: Int) {
        progress_view.setProgress(percent)
        count_duration_text_view.setPercent(percent)
    }

    fun getProgressView(): ColorfulProgressView {
        return progress_view
    }

    fun getCountPercentView(): CountSleepDurationTextView {
        return count_duration_text_view
    }
}