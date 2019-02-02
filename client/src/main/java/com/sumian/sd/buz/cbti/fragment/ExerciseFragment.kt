package com.sumian.sd.buz.cbti.fragment

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.sd.R
import com.sumian.sd.buz.cbti.activity.CBTIExerciseWebActivity
import com.sumian.sd.buz.cbti.activity.CBTIWeekCoursePartActivity.Companion.CHAPTER_ID
import com.sumian.sd.buz.cbti.adapter.ExerciseAdapter
import com.sumian.sd.buz.cbti.bean.Exercise
import com.sumian.sd.buz.cbti.presenter.CBTIWeekExercisesPresenter
import kotlinx.android.synthetic.main.fragment_tab_practice.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 练习tab
 *
 */
class ExerciseFragment : BaseViewModelFragment<CBTIWeekExercisesPresenter>(), BaseRecyclerAdapter.OnItemClickListener {

    private lateinit var mExerciseAdapter: ExerciseAdapter

    private var mChapterId = 1

    companion object {
        fun newInstance(chapterId: Int): ExerciseFragment {
            val args = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            val fragment = ExerciseFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        bundle.let {
            this.mChapterId = it.getInt(CHAPTER_ID, 0)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_practice
    }

    override fun initWidget() {
        super.initWidget()
        CBTIWeekExercisesPresenter.init(this)
        mExerciseAdapter = ExerciseAdapter(context!!)
        mExerciseAdapter.setOnItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mExerciseAdapter
    }

    override fun onResume() {
        super.onResume()
        this.mViewModel?.getCBTIWeekExercises(mChapterId)
    }

    fun setPresenter(presenter: CBTIWeekExercisesPresenter?) {
        //super.setPresenter(presenter)
        this.mViewModel = presenter
    }

    fun onGetCBTIWeekPracticeSuccess(exercises: List<Exercise>) {
        mExerciseAdapter.resetItem(exercises)
    }

    fun onGetCBTIWeekPracticeFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onItemClick(position: Int, itemId: Long) {

        val exercise = mExerciseAdapter.getItem(position)
        if (exercise.is_lock) {
            ToastUtils.showShort(R.string.see_lesson_2_unlock)
            return
        }

        CBTIExerciseWebActivity.show(activity!!, exercise.cbti_course_id)
    }

    fun onBegin() {

    }

    fun onFinish() {

    }
}