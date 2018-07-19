package com.sumian.sleepdoctor.cbti.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.cbti.activity.CBTIWeekLessonPartActivity.Companion.CHAPTER_ID
import com.sumian.sleepdoctor.cbti.adapter.ExerciseAdapter
import com.sumian.sleepdoctor.cbti.bean.Exercises
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekExercisesContract
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekExercisesPresenter
import kotlinx.android.synthetic.main.fragment_tab_practice.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 练习tab
 *
 */
class ExerciseFragment : BaseFragment<CBTIWeekExercisesContract.Presenter>(), CBTIWeekExercisesContract.View {

    private lateinit var mExerciseAdapter: ExerciseAdapter

    private var mChapterId = 1

    companion object {
        fun newInstance(chapterId: Int): LessonFragment {
            val args = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            return newInstance(LessonFragment::class.java, args) as LessonFragment
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

    override fun onGetCBTIWeekPracticeSuccess(exercises: Exercises) {
        mExerciseAdapter.resetItem(exercises.data)
    }

    override fun onGetCBTIWeekPracticeFailed(error: String) {
        showCenterToast(error)
    }

}