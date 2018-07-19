package com.sumian.sleepdoctor.cbti.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.cbti.fragment.ExerciseFragment
import com.sumian.sleepdoctor.cbti.fragment.LessonFragment
import com.sumian.sleepdoctor.cbti.model.CbtiChapterViewModel
import com.sumian.sleepdoctor.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_cbti_week_lesson_part.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 周阶段课时模块  包含一周的课时/练习  e.g.  1-1/1-2/1-3   2-1/2-2/2-3
 *
 */
class CBTIWeekLessonPartActivity : BaseActivity<CBTIWeekLessonContract.Presenter>(), TitleBar.OnBackClickListener, Observer<Courses> {

    private var mChapterId = 1

    companion object {

        const val CHAPTER_ID = "com.sumian.sleepdoctor.extras.chapter.id"

        fun show(context: Context, chapterId: Int) {
            val extras = Bundle().apply {
                putInt(CHAPTER_ID, chapterId)
            }
            show(context, CBTIWeekLessonPartActivity::class.java, extras)
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            mChapterId = it.getInt(CHAPTER_ID, 0)
        }
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_week_lesson_part
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICoursesLiveData().observe(this, this)
    }

    override fun initData() {
        super.initData()
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return LessonFragment.newInstance(mChapterId)
                    1 -> return ExerciseFragment.newInstance(mChapterId)
                }
                return LessonFragment()
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                when (position) {
                    0 -> return getString(R.string.lesson)
                    1 -> return getString(R.string.practice)
                }
                return super.getPageTitle(position)
            }
        }

        tab_layout.setupWithViewPager(view_pager, true)
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //TODO  主动同步课程和联系的对应节的状态
            }
        })
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onChanged(t: Courses?) {
        t?.let {
            cbti_week_lesson_banner_view.invalidateBanner(t.meta.chapter.title, t.meta.chapter.introduction, t.meta.chapter.banner, t.meta.chapter_progress)
        }
    }
}