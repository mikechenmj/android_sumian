package com.sumian.sd.service.diary

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_main_tel_booking.*

class DiaryEvaluationListActivity : BasePresenterActivity<IPresenter>() {
    private var mType = DiaryEvaluationListFragment.TYPE_UNFINISHED

    companion object {
        private const val KEY_TYPE = "type"

        fun getLaunchIntent(type: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), DiaryEvaluationListActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            return intent
        }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_diary_evaluation_list
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mType = bundle.getInt(KEY_TYPE, DiaryEvaluationListFragment.TYPE_UNFINISHED)
    }


    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.diary_evaluation)
        view_pager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return when (position) {
                    0 -> DiaryEvaluationListFragment.newInstance(DiaryEvaluationListFragment.TYPE_UNFINISHED)
                    1 -> DiaryEvaluationListFragment.newInstance(DiaryEvaluationListFragment.TYPE_FINISHED)
                    else -> throw IllegalStateException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.un_finished)
                    1 -> getString(R.string.is_finished)
                    else -> getString(R.string.un_finished)
                }
            }
        }
        tab_layout.setupWithViewPager(view_pager, true)
        view_pager.currentItem = if (mType == DiaryEvaluationListFragment.TYPE_UNFINISHED) 0 else 1
    }
}
