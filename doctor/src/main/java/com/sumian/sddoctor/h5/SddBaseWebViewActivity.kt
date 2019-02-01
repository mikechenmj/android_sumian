package com.sumian.sddoctor.h5

import android.annotation.SuppressLint
import com.sumian.common.h5.BaseWebViewActivity
import com.sumian.sddoctor.base.StatusBarHelper

@SuppressLint("Registered")
/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/23 11:37
 * desc   :
 * version: 1.0
 */
open class SddBaseWebViewActivity : BaseWebViewActivity() {

    override fun initWidget() {
        super.initWidget()
        StatusBarHelper.initTitleBarUI(this, mTitleBar)
    }
}