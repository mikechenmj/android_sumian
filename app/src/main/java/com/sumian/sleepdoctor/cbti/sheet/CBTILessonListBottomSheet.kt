package com.sumian.sleepdoctor.cbti.sheet

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.cbti.adapter.LessonListAdapter
import com.sumian.sleepdoctor.cbti.bean.Lesson
import com.sumian.sleepdoctor.widget.base.BaseBottomSheetView
import kotlinx.android.synthetic.main.lay_bottom_sheet_lesson_list.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:
 */
class CBTILessonListBottomSheet : BaseBottomSheetView() {

    private lateinit var mLessons: ArrayList<Lesson>

    companion object {

        private const val ARGS_LESSON_LIST = "com.sumian.sleepdoctor.args.lesson.list"

        fun show(fragmentManager: FragmentManager, lessons: List<Lesson>) {
            val cbtiLessonListBottomSheet = CBTILessonListBottomSheet()

            cbtiLessonListBottomSheet.arguments = Bundle().apply {
                putParcelableArrayList(ARGS_LESSON_LIST, lessons as ArrayList)
            }

            fragmentManager
                    .beginTransaction()
                    .add(cbtiLessonListBottomSheet, CBTILessonListBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            mLessons = it.getParcelableArrayList(ARGS_LESSON_LIST)
        }
    }

    override fun getLayout(): Int {
        return R.layout.lay_bottom_sheet_lesson_list
    }

    override fun initData() {
        super.initData()
        flow_layout.removeAllViewsInLayout()
        mLessons.let {
            it.forEachIndexed { index, lesson ->
                run {
                    val viewHolder = LessonListAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.lay_item_cbti_lesson_item, flow_layout, false))
                    viewHolder.initView(lesson)
                    viewHolder.itemView.tag = index
                    viewHolder.itemView.setOnClickListener {
                        val position = it.tag as Int
                    }

                    flow_layout.addView(viewHolder.itemView)
                }
            }
        }
    }


}