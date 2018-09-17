package com.sumian.hw.report

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.R
import com.sumian.sd.theme.base.BaseThemeActivity

/**
 * Created by dq
 *
 * on 2018/9/14
 *
 * desc:睡眠数据报告
 */
class ReportActivity : BaseThemeActivity() {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.startActivity(ReportActivity::class.java)
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_report
    }

    override fun initWidget() {
        super.initWidget()

        val findFragmentByTag = supportFragmentManager.findFragmentByTag(ReportFragment::class.java.simpleName)

        if (findFragmentByTag != null) {
            supportFragmentManager
                    .beginTransaction()
                    .show(findFragmentByTag).runOnCommit {
                        if (findFragmentByTag is ReportFragment) {
                            findFragmentByTag.onEnter(null)
                        }
                    }
                    .commitNowAllowingStateLoss()
        } else {

            val reportFragment = ReportFragment.newInstance()

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, reportFragment, ReportFragment::class.java.simpleName).runOnCommit {
                        reportFragment.onEnter(null)
                    }.commitNowAllowingStateLoss()
        }

    }
}