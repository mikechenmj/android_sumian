package com.sumian.hw.guideline.activity

import com.sumian.sd.BuildConfig
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity

/**
 * Created by sm
 * on 2018/3/27.
 *
 *
 * desc:新手指南,使用手册
 */

class ManualActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    override fun initWidget() {
        super.initWidget()
        getTitleBar().setIsDarkTheme(true)

    }

    override fun getCompleteUrl(): String {
        return BuildConfig.HW_USER_GUIDELINE_URL
    }
}
