package com.sumian.sd.service.cbti.fragment

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.service.cbti.activity.CBTIExerciseWebActivity
import com.sumian.sd.service.cbti.activity.CBTIWeekCoursePartActivity.Companion.CHAPTER_ID
import com.sumian.sd.service.cbti.adapter.ExerciseAdapter
import com.sumian.sd.service.cbti.bean.Exercise
import com.sumian.sd.service.cbti.contract.CBTIWeekExercisesContract
import com.sumian.sd.service.cbti.presenter.CBTIWeekExercisesPresenter
import kotlinx.android.synthetic.main.fragment_tab_practice.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 练习tab
 *
 */
class ExerciseFragment : SdBaseFragment<CBTIWeekExercisesContract.Presenter>(), CBTIWeekExercisesContract.View, BaseRecyclerAdapter.OnItemClickListener {

    private lateinit var mExerciseAdapter: ExerciseAdapter

    private var mChapterId = 1

    companion object {
        fun newInstance(chapterId: Int): ExerciseFragment {
            val args = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            return newInstance(ExerciseFragment::class.java, args) as ExerciseFragment
        }
    }

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        bundle?.let {
            this.mChapterId = it.getInt(CHAPTER_ID, 0)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_practice
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIWeekExercisesPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)

        mExerciseAdapter = ExerciseAdapter(context!!)
        mExerciseAdapter.setOnItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mExerciseAdapter
    }

    override fun onResume() {
        super.onResume()
        this.mPresenter.getCBTIWeekExercises(mChapterId)
    }

    override fun setPresenter(presenter: CBTIWeekExercisesContract.Presenter?) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onGetCBTIWeekPracticeSuccess(exercises: List<Exercise>) {
        mExerciseAdapter.resetItem(exercises)
    }

    override fun onGetCBTIWeekPracticeFailed(error: String) {
        showCenterToast(error)
    }

    override fun onItemClick(position: Int, itemId: Long) {

        val exercise = mExerciseAdapter.getItem(position)
        if (exercise.is_lock) {
            showCenterToast(R.string.see_lesson_2_unlock)
            return
        }

        CBTIExerciseWebActivity.show(activity!!, exercise.cbti_course_id)
    }

}