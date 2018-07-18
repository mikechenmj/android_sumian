package com.sumian.sleepdoctor.cbti.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.cbti.adapter.LessonAdapter
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.cbti.model.CbtiChapterViewModel
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
class LessonFragment : BaseFragment<CBTIWeekLessonContract.Presenter>(), CBTIWeekLessonContract.View, Observer<Courses> {

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
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().observe(this, this)
        this.mPresenter.getCBTIWeekLesson()
    }

    override fun onRelease() {
        super.onRelease()
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().removeObserver(this)
    }

    override fun setPresenter(presenter: CBTIWeekLessonContract.Presenter) {
        // super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onGetCBTIWeekLessonSuccess(courses: Courses) {
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).notifyCBTICourses(courses)
    }

    override fun onGetCBTIWeekLessonFailed(error: String) {
        showCenterToast(error)
    }

    override fun onChanged(t: Courses?) {
        if (t == null) {
            mLessonAdapter.clear()
        } else {
            mLessonAdapter.resetItem(t.data)
        }
    }
}