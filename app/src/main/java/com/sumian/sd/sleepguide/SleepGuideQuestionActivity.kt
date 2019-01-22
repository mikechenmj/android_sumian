package com.sumian.sd.sleepguide

import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R

class SleepGuideQuestionActivity : BasePresenterActivity<IPresenter>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_guide
    }

}
