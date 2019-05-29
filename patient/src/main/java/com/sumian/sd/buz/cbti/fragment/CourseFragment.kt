package com.sumian.sd.buz.cbti.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.sd.R
import com.sumian.sd.buz.account.achievement.MyAchievementShareActivity
import com.sumian.sd.buz.account.achievement.bean.Achievement
import com.sumian.sd.buz.account.achievement.bean.LastAchievementData
import com.sumian.sd.buz.account.achievement.contract.LastAchievementContract
import com.sumian.sd.buz.account.achievement.presenter.LastAchievementPresenter
import com.sumian.sd.buz.cbti.activity.CBTICoursePlayActivity
import com.sumian.sd.buz.cbti.activity.CBTIWeekCoursePartActivity.Companion.CHAPTER_ID
import com.sumian.sd.buz.cbti.adapter.CourseAdapter
import com.sumian.sd.buz.cbti.bean.CBTIMeta
import com.sumian.sd.buz.cbti.bean.Course
import com.sumian.sd.buz.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.buz.cbti.model.CbtiChapterViewModel
import com.sumian.sd.buz.cbti.presenter.CBTIWeekCoursePresenter
import kotlinx.android.synthetic.main.fragment_tab_lesson.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI 课程 tab
 *
 */
class CourseFragment : BaseViewModelFragment<CBTIWeekCoursePresenter>(), CBTIWeekLessonContract.View,
        Observer<List<Course>>, BaseRecyclerAdapter.OnItemClickListener, LastAchievementContract.View {

    private lateinit var mCourseAdapter: CourseAdapter

    private var mChapterId = 1

    companion object {
        fun newInstance(chapterId: Int): CourseFragment {
            val args = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            val fragment = CourseFragment()
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
        return R.layout.fragment_tab_lesson
    }

    override fun initWidget() {
        super.initWidget()
        CBTIWeekCoursePresenter.init(this)
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
        this.mViewModel?.getCBTIWeekLesson(mChapterId)
    }

    override fun onRelease() {
        super.onRelease()
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().removeObserver(this)
    }

    override fun setPresenter(presenter: CBTIWeekCoursePresenter) {
        // super.setPresenter(presenter)
        this.mViewModel = presenter
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val lesson = mCourseAdapter.getItem(position)
        if (lesson.is_lock) {
            ToastUtils.showShort(getString(R.string.finished_lesson_to_be_unlock))
            return
        }
        CBTICoursePlayActivity.show(activity!!, lesson, position)
    }

    override fun onGetCBTIWeekLessonSuccess(courses: List<Course>) {
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).notifyCourses(courses)
    }

    override fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta) {
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).notifyCBTICourseMeta(cbtiMeta)
        cbtiMeta.let {
            LastAchievementPresenter.init(this).getLastAchievement(achievementCategoryType = Achievement.CBTI_TYPE, achievementItemType = it.chapter.index)
        }
    }

    override fun onGetCBTIWeekLessonFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onGetAchievementListForTypeSuccess(lastAchievementData: LastAchievementData) {
        MyAchievementShareActivity.showFromLastAchievement(lastAchievementData)
    }


    override fun onChanged(t: List<Course>?) {
        if (t == null) {
            mCourseAdapter.clear()
        } else {
            mCourseAdapter.resetItem(t)
        }
    }
}