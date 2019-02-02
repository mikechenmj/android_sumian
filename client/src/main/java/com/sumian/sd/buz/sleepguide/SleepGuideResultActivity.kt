package com.sumian.sd.buz.sleepguide

import com.sumian.common.base.BaseActivity
import com.sumian.sd.R

class SleepGuideResultActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_guide_result
    }

    override fun showBackNav(): Boolean {
        return true
    }
}
