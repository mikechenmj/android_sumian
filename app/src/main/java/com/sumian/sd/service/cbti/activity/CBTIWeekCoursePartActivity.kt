package com.sumian.sd.service.cbti.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.CBTIDataResponse
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.bean.Course
import com.sumian.sd.service.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.service.cbti.fragment.CourseFragment
import com.sumian.sd.service.cbti.fragment.ExerciseFragment
import com.sumian.sd.service.cbti.model.CbtiChapterViewModel
import com.sumian.sd.widget.TitleBar
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

    override fun initWidget(root: View?) {
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
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //TODO  主动同步课程和联系的对应节的状态
            }
        })
        initTitle()
    }

    private fun initTitle() {
        val call = AppManager.getSdHttpService().getCBTICourseWeekPart(mChapterId)
        call.enqueue(object : BaseSdResponseCallback<CBTIDataResponse<Course>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: CBTIDataResponse<Course>?) {
                response?.let {
                    title_bar.setTitle(response.meta.chapter.title)
                }
            }
        })
        addCall(call)
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let {
            cbti_week_lesson_banner_view.invalidateBanner(t.chapter.title, t.chapter.introduction, t.chapter.banner, t.chapter_progress)
        }
    }
}