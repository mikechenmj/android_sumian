package com.sumian.sddoctor.service.cbti.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseActivity
import com.sumian.sddoctor.service.cbti.bean.CBTIMeta
import com.sumian.sddoctor.service.cbti.contract.CBTIMessageBoardActionContract
import com.sumian.sddoctor.service.cbti.contract.CBTIWeekLessonContract
import com.sumian.sddoctor.service.cbti.dialog.SumianDataWebDialog
import com.sumian.sddoctor.service.cbti.fragment.CourseFragment
import com.sumian.sddoctor.service.cbti.model.CbtiChapterViewModel
import com.sumian.sddoctor.service.cbti.presenter.CBTIMessageBoardActionPresenter
import com.sumian.sddoctor.service.cbti.widget.keyboard.MsgBoardKeyBoard
import com.sumian.sddoctor.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_cbti_week_lesson_part.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 周阶段课程模块  包含一周的课时/练习  e.g.  1-1/1-2/1-3   2-1/2-2/2-3
 *
 */
@Suppress("DEPRECATION")
class CBTIWeekCoursePartActivity : SdBaseActivity<CBTIWeekLessonContract.Presenter>(), TitleBar.OnBackClickListener,
        Observer<CBTIMeta>, MsgBoardKeyBoard.OnKeyBoardCallback, CBTIMessageBoardActionContract.View {

    private var mChapterId = 1
    private var mCBTIType = 1

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

    override fun initPresenter() {
        super.initPresenter()
        CBTIMessageBoardActionPresenter.init(this)
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
        view_pager.adapter = initAdapter()
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun sendContent(content: String, anonymousType: Int) {
        //CBTIMessageBoardActionPresenter.init(this).publishMessage(message = content, type = mCBTIType, isAnonymous =
        //anonymousType)
    }

    override fun close() {
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let { it ->
            this.mCBTIType = t.chapter.index
            title_bar.setTitle(it.chapter.title)
            cbti_week_lesson_banner_view.invalidateBanner(it.chapter.title, it.chapter.introduction, it.chapter.banner, it.chapter_progress)
            nav_tab_lesson_summary.setOnClickListener {
                //val url = H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", t.chapter.summary) + "&token=" + AppManager.getAccountViewModel().token.token
                SumianDataWebDialog(null, resources.getString(R.string.the_week_lesson_summary), t.chapter.summary_rtf).show(supportFragmentManager)
            }
        }
    }

    override fun onPublishMessageBoardSuccess(success: String) {
        //keyboard.hide()
        showCenterToast(success)
    }

    override fun onPublishMessageBoardFailed(error: String) {
        showCenterToast(error)
    }

    private fun initAdapter(): FragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CourseFragment.newInstance(mChapterId)
                else -> {
                    throw NullPointerException("index is invalid")
                }
            }
        }

        override fun getCount(): Int {
            return 1
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.course)
                else -> {
                    throw NullPointerException("index is invalid")
                }
            }
        }
    }
}