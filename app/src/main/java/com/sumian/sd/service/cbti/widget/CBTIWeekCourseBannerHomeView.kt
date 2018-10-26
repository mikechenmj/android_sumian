@file:Suppress("DEPRECATION")

package com.sumian.sd.service.cbti.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_cbti_lesson_banner_home_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc: CBTI  a week lesson banner view
 */
class CBTIWeekCourseBannerHomeView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        orientation = VERTICAL
        inflate(context, R.layout.lay_cbti_lesson_banner_home_view, this)
        setBackgroundResource(R.drawable.ic_cbti_banner)
    }

    fun invalidateBanner(formatExpiredTime: String, formatTotalProgress: String) {
        cbti_lesson_plan_view.invalidView(formatExpiredTime, formatTotalProgress)
    }
}