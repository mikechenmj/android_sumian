package com.sumian.sd.service.cbti.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.service.cbti.fragment.CourseFragment
import com.sumian.sd.service.cbti.fragment.ExerciseFragment
import com.sumian.sd.service.cbti.model.CbtiChapterViewModel
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.SumianWebDialog
import kotlinx.android.synthetic.main.activity_main_cbti_week_lesson_part.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 周阶段课程模块  包含一周的课时/练习  e.g.  1-1/1-2/1-3   2-1/2-2/2-3
 *
 */
class CBTIWeekCoursePartActivity : SdBaseActivity<CBTIWeekLessonContract.Presenter>(), TitleBar.OnBackClickListener, Observer<CBTIMeta> {

    private var mChapterId = 1

    companion object {

        const val CHAPTER_ID = "com.sumian.sleepdoctor.extras.chapter.id"

        fun show(context: Context, chapterId: Int) {
            ActivityUtils.startActivity(getLaunchIntent(context, chapterId))
        }

        fun getLaunchIntent(context: Context, chapterId: Int): Intent {
            val intent = Intent(context, CBTIWeekCoursePartActivity::class.java)
            intent.putExtra(CHAPTER_ID, chapterId)
            return intent
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

    override fun initWidget(root: View) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICourseMetaLiveData().observe(this, this)
    }

    override fun initData() {
        super.initData()
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> CourseFragment.newInstance(mChapterId)
                    1 -> ExerciseFragment.newInstance(mChapterId)
                    else -> {
                        throw NullPointerException("index is invalid")
                    }
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                when (position) {
                    0 -> return getString(R.string.course)
                    1 -> return getString(R.string.practice)
                }
                return super.getPageTitle(position)
            }
        }

        tab_layout.setupWithViewPager(view_pager, true)
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let { it ->
            title_bar.setTitle(it.chapter.title)
            cbti_week_lesson_banner_view.invalidateBanner(it.chapter.title, it.chapter.introduction, it.chapter.banner, it.chapter_progress)

            nav_tab_lesson_summary.setOnClickListener {
                val url = H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", t.chapter.summary.replace("\r\n", "<br>")) + "&token=" + AppManager.getAccountViewModel().token.token
                SumianWebDialog.createWithPartUrl(url, resources.getString(R.string.the_week_lesson_summary)).show(supportFragmentManager)
            }

            v_divider.visibility = if (!TextUtils.isEmpty(it.last_chapter_summary)) View.VISIBLE else View.GONE
            nav_tab_lesson_review_last_week.visibility = if (!TextUtils.isEmpty(it.last_chapter_summary)) View.VISIBLE else View.GONE
            nav_tab_lesson_review_last_week.setOnClickListener {
                val url = H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", t.last_chapter_summary!!.replace("\r\n", "<br>")) + "&token=" + AppManager.getAccountViewModel().token.token
                SumianWebDialog.createWithPartUrl(url, resources.getString(R.string.lesson_review_last_week)).show(supportFragmentManager)
            }
        }
    }
}