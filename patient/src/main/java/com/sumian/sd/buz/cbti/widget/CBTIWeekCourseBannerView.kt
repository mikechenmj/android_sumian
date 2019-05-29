@file:Suppress("DEPRECATION")

package com.sumian.sd.buz.cbti.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_cbti_lesson_banner_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc: CBTI  a week lesson banner view
 */
class CBTIWeekCourseBannerView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        inflate(context, R.layout.lay_cbti_lesson_banner_view, this)
        orientation = VERTICAL
        // setBackgroundResource(R.mipmap.ic_img_cbti_banner)
    }

    fun invalidateBanner(title: String, desc: String, bannerUrl: String, lessonPlanProgress: Int) {

        tv_cbti_week_lesson_title.text = title
        tv_cbti_week_lesson_desc.text = desc
        Glide.with(this).asDrawable().load(bannerUrl).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                background = resource
            }
        })
        cbti_lesson_plan_view.invalidate(lessonPlanProgress)
    }
}