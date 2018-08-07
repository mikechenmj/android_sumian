package com.sumian.sleepdoctor.cbti.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.SdBaseFragment
import com.sumian.sleepdoctor.cbti.activity.CBTICoursePlayActivity
import com.sumian.sleepdoctor.cbti.activity.CBTIWeekCoursePartActivity.Companion.CHAPTER_ID
import com.sumian.sleepdoctor.cbti.adapter.CourseAdapter
import com.sumian.sleepdoctor.cbti.bean.CBTIMeta
import com.sumian.sleepdoctor.cbti.bean.Course
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.cbti.model.CbtiChapterViewModel
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekCoursePresenter
import kotlinx.android.synthetic.main.fragment_tab_lesson.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI 课程 tab
 *
 */
class CourseFragment : SdBaseFragment<CBTIWeekLessonContract.Presenter>(), CBTIWeekLessonContract.View, Observer<List<Course>>, BaseRecyclerAdapter.OnItemClickListener {

    private lateinit var mCourseAdapter: CourseAdapter

    private var mChapterId = 1

    companion object {
        fun newInstance(chapterId: Int): CourseFragment {
            val args = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            return newInstance(CourseFragment::class.java, args) as CourseFragment
        }
    }

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        bundle?.let {
            this.mChapterId = it.getInt(CHAPTER_ID, 0)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_lesson
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIWeekCoursePresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        mCourseAdapter = CourseAdapter(context!!)
        mCourseAdapter.setOnItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mCourseAdapter
    }

    override fun initData() {
        super.initData()
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().observe(this, this)
    }

    override fun onResume() {
        super.onResume()
        this.mPresenter.getCBTIWeekLesson(mChapterId)
    }

    override fun onRelease() {
        super.onRelease()
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().removeObserver(this)
    }

    override fun setPresenter(presenter: CBTIWeekLessonContract.Presenter) {
        // super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val lesson = mCourseAdapter.getItem(position)
        if (lesson.is_lock) {
            showCenterToast("完成上节课程后解锁")
            return
        }
        CBTICoursePlayActivity.show(activity!!, lesson, position)
    }

    override fun onGetCBTIWeekLessonSuccess(courses: List<Course>) {
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).notifyCourses(courses)
    }

    override fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta) {
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).notifyCBTICourseMeta(cbtiMeta)
    }

    override fun onGetCBTIWeekLessonFailed(error: String) {
        showCenterToast(error)
    }

    override fun onChanged(t: List<Course>?) {
        if (t == null) {
            mCourseAdapter.clear()
        } else {
            mCourseAdapter.resetItem(t)
        }
    }
}