package com.sumian.sd.buz.diary.fillsleepdiary

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.SleepRestrictionIntroductionDialogActivity
import com.sumian.sd.buz.stat.StatConstants

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/25 15:11
 * desc   :
 * version: 1.0
 */
class SleepDiaryActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary
    }

    override fun getPageName(): String {
        return StatConstants.page_sleep_diary_detail
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.enter_sleep_diary_detail_page)
        setTitle(R.string.sleep_diary)
        if (intent.getBooleanExtra(KEY_SHOW_DIALOG, false)) {
            SleepRestrictionIntroductionDialogActivity.start()
        }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    companion object {
        private const val KEY_SHOW_DIALOG = "KEY_SHOW_DIALOG"

        fun start(showDialog: Boolean) {
            val bundle = Bundle()
            bundle.putBoolean(KEY_SHOW_DIALOG, showDialog)
            ActivityUtils.startActivity(bundle, SleepDiaryActivity::class.java)
        }
    }
}