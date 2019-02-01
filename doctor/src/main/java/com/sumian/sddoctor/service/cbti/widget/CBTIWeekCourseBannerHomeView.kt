@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.service.cbti.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sumian.sddoctor.R
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

    fun invalidateBanner(formatTotalProgress: String) {
        cbti_lesson_plan_view.invalidView(formatTotalProgress)
    }

    fun invalidateBannerExtras(bannerUrl: String, name: String, introduction: String) {
        tv_cbti_name.text = name
        tv_cbti_introduction.text = introduction
        Glide.with(this).asDrawable().load(bannerUrl).apply(RequestOptions.placeholderOf(R.drawable.ic_cbti_banner).error(R.drawable.ic_cbti_banner)).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                background = resource
            }
        })
    }
}