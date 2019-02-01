package com.sumian.sd.buz.cbti.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.tabs.TabLayout
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.buz.cbti.bean.CBTIMeta
import com.sumian.sd.buz.cbti.contract.CBTIMessageBoardActionContract
import com.sumian.sd.buz.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.buz.cbti.fragment.CourseFragment
import com.sumian.sd.buz.cbti.fragment.ExerciseFragment
import com.sumian.sd.buz.cbti.fragment.MessageBoardFragment
import com.sumian.sd.buz.cbti.model.CbtiChapterViewModel
import com.sumian.sd.buz.cbti.presenter.CBTIMessageBoardActionPresenter
import com.sumian.sd.buz.cbti.widget.adapter.EmptyOnTabSelectedListener
import com.sumian.sd.buz.cbti.widget.keyboard.MsgBoardKeyBoard
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.SumianDataWebDialog
import kotlinx.android.synthetic.main.activity_main_cbti_week_lesson_part.*

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 周阶段课程模块  包含一周的课时/练习  e.g.  1-1/1-2/1-3   2-1/2-2/2-3
 *
 */
class CBTIWeekCoursePartActivity : SdBaseActivity<CBTIWeekLessonContract.Presenter>(), TitleBar.OnBackClickListener,
        Observer<CBTIMeta>, MsgBoardKeyBoard.OnKeyBoardCallback, CBTIMessageBoardActionContract.View {

    private var mChapterId = 1
    private var mCbtiType = 1

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
        //view_pager.offscreenPageLimit = 2
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                showMessageBoard(position)
            }
        })
        tab_layout.setupWithViewPager(view_pager, true)
        tab_layout.addOnTabSelectedListener(object : EmptyOnTabSelectedListener() {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                showMessageBoard(tab?.position!!)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                showMessageBoard(tab?.position!!)
            }
        })
        tv_write_message.setOnClickListener {
            //tv_write_message.visibility = View.GONE
            //   keyboard.show()
            CBTIMessageBoardActivity.show(mCbtiType)
        }
        // keyboard.setOnKeyBoardCallback(this)
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
        //CBTIMessageBoardActionPresenter.init(this).publishMessage(message = content, type = mCbtiType, isAnonymous =
        //anonymousType)
    }

    override fun close() {
        showMessageBoard(2)
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let { it ->

            this.mCbtiType = t.chapter.index
            title_bar.setTitle(it.chapter.title)
            cbti_week_lesson_banner_view.invalidateBanner(it.chapter.title, it.chapter.introduction, it.chapter.banner, it.chapter_progress)

            nav_tab_lesson_summary.visibility = if (it.chapter_progress == 100) View.VISIBLE else View.GONE
            nav_tab_lesson_summary.setOnClickListener {
                //val url = H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", t.chapter.summary) + "&token=" + AppManager.getAccountViewModel().token.token
                SumianDataWebDialog(null, resources.getString(R.string.the_week_lesson_summary), t.chapter.summary_rtf).show(supportFragmentManager)
            }

            v_divider.visibility = if (!TextUtils.isEmpty(it.last_chapter_summary_rtf)) View.VISIBLE else View.GONE
            nav_tab_lesson_review_last_week.visibility = if (!TextUtils.isEmpty(it.last_chapter_summary_rtf)) View.VISIBLE else View.GONE
            nav_tab_lesson_review_last_week.setOnClickListener {
                //val url = H5Uri.CBTI_WEEK_REVIEW.replace("{last_chapter_summary}", t.last_chapter_summary!!) + "&token=" + AppManager.getAccountViewModel().token.token
                SumianDataWebDialog(null, resources.getString(R.string.lesson_review_last_week), t.last_chapter_summary_rtf).show(supportFragmentManager)
            }

            lay_lesson_tips.visibility = if (it.chapter_progress == 100 || !TextUtils.isEmpty(it.last_chapter_summary_rtf)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onPublishMessageBoardSuccess(success: String) {
        showMessageBoard(2)
        //keyboard.hide()
        showCenterToast(success)
    }

    override fun onPublishMessageBoardFailed(error: String) {
        showCenterToast(error)
    }

    private fun showMessageBoard(position: Int) {
        tv_write_message?.visibility = if (position == 2) {
            View.VISIBLE
        } else {
            View.GONE
        }
        //keyboard.hide()
    }

    private fun initAdapter(): FragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CourseFragment.newInstance(mChapterId)
                1 -> ExerciseFragment.newInstance(mChapterId)
                2 -> MessageBoardFragment.newInstance(mCbtiType)
                else -> {
                    throw NullPointerException("index is invalid")
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.course)
                1 -> getString(R.string.practice)
                2 -> getString(R.string.message_board)
                else -> {
                    super.getPageTitle(position)
                }

            }

        }
    }
}