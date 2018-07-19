package com.sumian.sleepdoctor.cbti.widget

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.cbti.adapter.CourseListAdapter
import com.sumian.sleepdoctor.cbti.bean.Course
import kotlinx.android.synthetic.main.lay_cbti_lesson_list_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:CBTI 详情页当中,课程列表 view
 */
class CBTICourseListView : LinearLayout {

    private lateinit var mCourseListAdapter: CourseListAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }


    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_cbti_lesson_list_view, this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.itemAnimator = DefaultItemAnimator()
        mCourseListAdapter = CourseListAdapter(context)
        recycler.adapter = mCourseListAdapter
    }

    fun setShowLessonListBottomSheet(onClickListener: OnClickListener) {
        iv_lesson_list.setOnClickListener(onClickListener)
    }

    fun addAllItem(items: List<Course>) {
        mCourseListAdapter.addAll(items)
    }

    fun addItem(item: Course) {
        mCourseListAdapter.addItem(item)
    }

    fun updateItem(item: Course) {
        mCourseListAdapter.items.forEachIndexed { index, lesson ->
            run {
                if (lesson.id == item.id) {
                    mCourseListAdapter.updateItem(index)
                }
            }
        }
    }

}