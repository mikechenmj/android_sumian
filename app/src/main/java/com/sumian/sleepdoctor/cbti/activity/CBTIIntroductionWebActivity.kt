package com.sumian.sleepdoctor.cbti.activity

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseWebViewActivity
import com.sumian.sleepdoctor.h5.H5Uri

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:
 *
 */
class CBTIIntroductionWebActivity : BaseWebViewActivity<BasePresenter<*>>() {

    override fun initTitle(): String {
        return "CBTI详细介绍, web页面"
    }

    override fun getUrlContentPart(): String? {
        return H5Uri.SLEEP_RECORD_RECORD_SLEEP
    }
}