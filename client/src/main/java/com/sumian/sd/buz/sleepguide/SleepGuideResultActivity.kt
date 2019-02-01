package com.sumian.sd.buz.sleepguide

import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R

class SleepGuideResultActivity : BasePresenterActivity<IPresenter>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_guide_result
    }

    override fun showBackNav(): Boolean {
        return true
    }
}
