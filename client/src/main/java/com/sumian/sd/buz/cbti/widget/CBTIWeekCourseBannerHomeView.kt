@file:Suppress("DEPRECATION")

package com.sumian.sd.buz.cbti.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sumian.common.image.ImageLoader
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
//        iv_banner.setImageResource(R.drawable.ic_cbti_img_banner1)
    }

    fun invalidateBanner(formatExpiredTime: String, formatTotalProgress: String?) {
        cbti_lesson_plan_view.invalidView(formatExpiredTime, formatTotalProgress ?: "")
    }

    fun invalidateBannerExtras(bannerUrl: String, name: String, introduction: String) {
        tv_cbti_name.text = name
        tv_cbti_introduction.text = introduction
        ImageLoader.loadImage(bannerUrl, iv_banner, R.drawable.ic_cbti_img_banner1, R.drawable.ic_cbti_img_banner1)
    }
}