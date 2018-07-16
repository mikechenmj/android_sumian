package com.sumian.sleepdoctor.cbti.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
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

    override fun initData() {
        super.initData()
        this.mPresenter.getCBTIWeekExercises()
    }

    override fun setPresenter(presenter: CBTIWeekExercisesContract.Presenter?) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onGetCBTIWeekPracticeSuccess(exercises: Exercises) {
        mExerciseAdapter.addAll(exercises.data)
    }

    override fun onGetCBTIWeekPracticeFailed(error: String) {
        showCenterToast(error)
    }

}