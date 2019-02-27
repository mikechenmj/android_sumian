package com.sumian.sddoctor.service.evaluation.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.service.evaluation.bean.WeekEvaluation
import com.sumian.sddoctor.service.evaluation.fragment.WeekEvaluationListFragment
import kotlinx.android.synthetic.main.activity_main_evaluation.*

@Suppress("DEPRECATION")
/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 睡眠日记周评估列表
 */
class WeekEvaluationListActivity : SddBaseActivity() {

    companion object {

        @JvmStatic
        fun show() {
            val topActivity = ActivityUtils.getTopActivity()
            topActivity?.let {
                it.startActivity(Intent(it, WeekEvaluationListActivity::class.java))
            }
        }

    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_evaluation
    }

    override fun getPageName(): String {
        return StatConstants.page_service_dairy_evaluate
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(getString(R.string.diary_evaluation))
        view_pager.offscreenPageLimit = 5
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> WeekEvaluationListFragment.newInstance(WeekEvaluation.ALL_TYPE)
                    1 -> WeekEvaluationListFragment.newInstance(WeekEvaluation.REPLYING_TYPE)
                    2 -> WeekEvaluationListFragment.newInstance(WeekEvaluation.FINISHED_TYPE)
                    3 -> WeekEvaluationListFragment.newInstance(WeekEvaluation.CANCEL_TYPE)
                    4 -> WeekEvaluationListFragment.newInstance(WeekEvaluation.CLOSED_TYPE)
                    else -> throw IllegalArgumentException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 5
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "全部"
                    1 -> "待回复"
                    2 -> "已完成"
                    3 -> "已取消"
                    4 -> "已关闭"
                    else -> throw IllegalArgumentException("invalid position")
                }
            }
        }

        tab_layout?.setupWithViewPager(view_pager, true)
    }
}
