package com.sumian.sd.service.cbti.activity

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI  h5介绍页  即了解更多那里跳转
 *
 */
class CBTIIntroduction2WebActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroduction2WebActivity::class.java))
            }
        }
    }

    override fun initWidget() {
        super.initWidget()
        getTitleBar().openTopPadding(true)
    }

    override fun getUrlContentPart(): String? {
        return H5Uri.CBTI_INTRODUCTION
    }
}