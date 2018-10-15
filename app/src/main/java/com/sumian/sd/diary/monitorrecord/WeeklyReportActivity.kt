package com.sumian.sd.diary.monitorrecord

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackActivity
import com.sumian.hw.report.fragment.WeeklyReportFragment
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 16:37
 * desc   :
 * version: 1.0
 */
class WeeklyReportActivity : BaseBackActivity() {

    companion object {
        private const val KEY_SCROLL_TIME = "scroll_time"

        fun launch(time: Long) {
            val intent = Intent(ActivityUtils.getTopActivity(), WeeklyReportActivity::class.java)
            intent.putExtra(KEY_SCROLL_TIME, time)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_weekly_report
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.weekly_data)
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, WeeklyReportFragment.newInstance(intent.getLongExtra(KEY_SCROLL_TIME, 0))).commitNowAllowingStateLoss()
    }
}