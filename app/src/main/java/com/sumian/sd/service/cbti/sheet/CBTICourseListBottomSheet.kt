package com.sumian.sd.service.cbti.sheet

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.service.cbti.adapter.CourseListAdapter
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.bean.Course
import com.sumian.sd.service.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.service.cbti.presenter.CBTIWeekCoursePresenter
import com.sumian.sd.widget.base.BaseBottomSheetView
import kotlinx.android.synthetic.main.lay_bottom_sheet_lesson_list.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:
 */
class CBTICourseListBottomSheet : BaseBottomSheetView(), CBTIWeekLessonContract.View {

    private var mChapterId: Int = 1
    private var mPosition = 0

    companion object {

        private const val ARGS_CHAPTER_ID = "com.sumian.sleepdoctor.args.chapter.id"
        private const val ARGS_SELECT_POSITION = "com.sumian.sleepdoctor.args.select.position"

        fun show(fragmentManager: androidx.fragment.app.FragmentManager, ChapterId: Int, position: Int, onCBTILessonListCallback: OnCBTILessonListCallback) {
            val cbtiLessonListBottomSheet = CBTICourseListBottomSheet().setOnCbtiLessonListCallback(onCBTILessonListCallback)

            cbtiLessonListBottomSheet.arguments = Bundle().apply {
                putInt(ARGS_CHAPTER_ID, ChapterId)
                putInt(ARGS_SELECT_POSITION, position)
            }

            fragmentManager
                    .beginTransaction()
                    .add(cbtiLessonListBottomSheet, CBTICourseListBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    private var cbtiLessonListCallback: OnCBTILessonListCallback? = null

    fun setOnCbtiLessonListCallback(onCBTILessonListCallback: OnCBTILessonListCallback): CBTICourseListBottomSheet {
        this.cbtiLessonListCallback = onCBTILessonListCallback
        return this
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            mChapterId = it.getInt(ARGS_CHAPTER_ID, 0)
            mPosition = it.getInt(ARGS_SELECT_POSITION, 0)
        }
    }

    override fun getLayout(): Int {
        return R.layout.lay_bottom_sheet_lesson_list
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)
        cbtiLessonListCallback?.onShow()
    }

    override fun initData() {
        super.initData()
        CBTIWeekCoursePresenter.init(this).getCBTIWeekLesson(mChapterId)
    }

    override fun onGetCBTIWeekLessonSuccess(courses: List<Course>) {
        invalidateItem(mPosition, courses)
    }

    override fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta) {

    }

    override fun onGetCBTIWeekLessonFailed(error: String) {
        ToastHelper.show(context, error, Gravity.CENTER)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        cbtiLessonListCallback?.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        cbtiLessonListCallback?.onDismiss()
    }

    private fun invalidateItem(position: Int, courses: List<Course>?) {
        flow_layout.removeAllViews()
        courses?.let { it ->
            it.forEachIndexed { index, lesson ->
                run {
                    val viewHolder = CourseListAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.lay_item_cbti_lesson_item, flow_layout, false))
                    courses[position].current_course = true
                    viewHolder.initView(lesson)
                    viewHolder.itemView?.tag = index
                    viewHolder.itemView?.setOnClickListener {
                        val p = it.tag as Int
                        if (position == p) {
                            return@setOnClickListener
                        }
                        val tmpCourse = courses[p]
                        Log.e("TAG", "$p")
                        if (tmpCourse.is_lock) {
                            ToastHelper.show(it.context, it.context.getString(R.string.finished_pre_lesson_2_unlock), Gravity.CENTER)
                            return@setOnClickListener
                        }
                        courses.forEachIndexed { index, _ ->
                            run {
                                courses[index].current_course = p == index
                            }
                        }
                        invalidateItem(p, courses)
                        val currentLesson = courses[p]
                        if (cbtiLessonListCallback?.onSelectLesson(p, currentLesson)!!) {
                            dismissAllowingStateLoss()
                        }
                    }
                    flow_layout.addView(viewHolder.itemView)
                }
            }
        }
    }

    interface OnCBTILessonListCallback {

        fun onSelectLesson(position: Int, course: Course): Boolean

        fun onShow()

        fun onDismiss()

    }
}