package com.sumian.sd.service.diary

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.sumian.common.base.BaseBackActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_main_tel_booking.*

class DiaryEvaluationListActivity : BaseBackActivity() {

    override fun getChildContentId(): Int {
        return R.layout.activity_diary_evaluation_list
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.diary_evaluation)
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
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
    }
}
