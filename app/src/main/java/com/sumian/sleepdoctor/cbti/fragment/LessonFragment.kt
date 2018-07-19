package com.sumian.sleepdoctor.cbti.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.cbti.activity.CBTILessonDetailActivity
import com.sumian.sleepdoctor.cbti.activity.CBTIWeekLessonPartActivity.Companion.CHAPTER_ID
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
class LessonFragment : BaseFragment<CBTIWeekLessonContract.Presenter>(), CBTIWeekLessonContract.View, Observer<Courses>, BaseRecyclerAdapter.OnItemClickListener {

    private lateinit var mLessonAdapter: LessonAdapter

    private var mChapterId = 0

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
        return R.layout.fragment_tab_lesson
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIWeekLessonPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        mLessonAdapter = LessonAdapter(context!!)
        mLessonAdapter.setOnItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mLessonAdapter
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
        val lesson = mLessonAdapter.getItem(position)
        if (lesson.is_lock) {
            return
        }
        CBTILessonDetailActivity.show(activity!!, lesson.id)
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