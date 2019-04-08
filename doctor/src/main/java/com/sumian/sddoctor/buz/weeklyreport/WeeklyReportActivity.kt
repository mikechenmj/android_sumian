package com.sumian.sddoctor.buz.weeklyreport

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.h5.SimpleWebActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/1 16:44
 * desc   :
 * version: 1.0
 */
class WeeklyReportActivity : SimpleWebActivity() {

    companion object {
        fun launch(date: Int) {
            ActivityUtils.startActivity(getLaunchIntent(date))
        }

        fun getLaunchIntent(date: Int): Intent {
            val page = mapOf("page" to "weeklyReport", "payload" to mapOf("date" to date))
            return SimpleWebActivity.getLaunchIntentWithRouteData(ActivityUtils.getTopActivity(), JsonUtil.toJson(page), WeeklyReportActivity::class.java)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_weekly_report
    }

}