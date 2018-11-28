package com.sumian.hw.report.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

import com.sumian.sd.R

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class ReportIndicatorRuleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        gravity = Gravity.CENTER
        orientation = LinearLayout.HORIZONTAL
        View.inflate(context, R.layout.hw_lay_report_sleep_rule, this)
    }
}
