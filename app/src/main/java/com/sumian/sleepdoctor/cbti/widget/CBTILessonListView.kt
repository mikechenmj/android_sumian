package com.sumian.sleepdoctor.cbti.widget

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.cbti.adapter.LessonListAdapter
import com.sumian.sleepdoctor.cbti.bean.Lesson
import kotlinx.android.synthetic.main.lay_cbti_lesson_list_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:CBTI 详情页当中,课程列表 view
 */
class CBTILessonListView : LinearLayout {

    private lateinit var mLessonListAdapter: LessonListAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }


    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_cbti_lesson_list_view, this)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.itemAnimator = DefaultItemAnimator()
        mLessonListAdapter = LessonListAdapter(context)
        recycler.adapter = mLessonListAdapter
    }

    fun setShowLessonListBottomSheet(onClickListener: OnClickListener) {
        iv_lesson_list.setOnClickListener(onClickListener)
    }

    fun addAllItem(items: List<Lesson>) {
        mLessonListAdapter.addAll(items)
    }

    fun addItem(item: Lesson) {
        mLessonListAdapter.addItem(item)
    }

    fun updateItem(item: Lesson) {
        mLessonListAdapter.items.forEachIndexed { index, lesson ->
            run {
                if (lesson.id == item.id) {
                    mLessonListAdapter.updateItem(index)
                }
            }
        }
    }

}