package com.sumian.sleepdoctor.cbti.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.cbti.adapter.LessonAdapter
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekLessonPresenter
import kotlinx.android.synthetic.main.fragment_tab_lesson.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI 课程 tab
 *
 */
class LessonFragment : BaseFragment<CBTIWeekLessonContract.Presenter>(), CBTIWeekLessonContract.View {

    private lateinit var mLessonAdapter: LessonAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_lesson
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIWeekLessonPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        mLessonAdapter = LessonAdapter(context!!)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mLessonAdapter
    }

    override fun initData() {
        super.initData()

        this.mPresenter.getCBTIWeekLesson()
    }

    override fun setPresenter(presenter: CBTIWeekLessonContract.Presenter) {
        // super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onGetCBTIWeekLessonSuccess(courses: Courses) {

        mLessonAdapter.addAll(courses.data)
    }

    override fun onGetCBTIWeekLessonFailed(error: String) {
        showCenterToast(error)
    }
}