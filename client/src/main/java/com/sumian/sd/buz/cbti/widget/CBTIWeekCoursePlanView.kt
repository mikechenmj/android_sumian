package com.sumian.sd.buz.cbti.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_cbti_lesson_plan_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc: 一周课时进度计划  view
 */
class CBTIWeekCoursePlanView : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        setPadding(resources.getDimensionPixelOffset(R.dimen.space_20), 0, resources.getDimensionPixelOffset(R.dimen.space_20), 0)
        View.inflate(context, R.layout.lay_cbti_lesson_plan_view, this)
    }

    @SuppressLint("SetTextI18n")
    fun invalidate(lessonProgress: Int = 0) {
        if (lessonProgress >= 100) {
            tv_lesson_finished.visibility = View.VISIBLE
            lay_lesson_progress_container.visibility = View.GONE
        } else {
            tv_lesson_finished.visibility = View.GONE
            lesson_pb.progress = lessonProgress
            tv_percent.text = "$lessonProgress%"
            lay_lesson_progress_container.visibility = View.VISIBLE
        }
        show()
    }


    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }
}