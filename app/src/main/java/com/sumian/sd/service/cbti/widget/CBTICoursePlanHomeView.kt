package com.sumian.sd.service.cbti.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R
import com.sumian.sd.utils.TimeUtil
import com.sumian.sd.utils.getString
import kotlinx.android.synthetic.main.lay_cbti_lesson_introduction_home_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc: CBTI 总课时进度计划/介绍/到期时间  view
 */
class CBTICoursePlanHomeView : ConstraintLayout, IVisible {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_cbti_lesson_introduction_home_view, this)
    }

    fun invalidCBTICOurse(progress: Int, totalProgress: Int, invalidTime: Int) {
        tv_cbti_progress.text = String.format(context.getString(R.string.cbti_course_plan_introduction), "$progress", "$totalProgress")
        val formatDate = TimeUtil.formatDate("yyyy.mm.dd", invalidTime * 1000L)
        tv_cbti_invalid_time.text = String.format(getString(R.string.cbti_invalid_time), formatDate)
        tv_cbti_introduction.setOnClickListener {

        }
    }


    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }
}