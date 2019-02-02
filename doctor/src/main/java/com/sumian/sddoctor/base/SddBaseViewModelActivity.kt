package com.sumian.sddoctor.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 16:09
 * desc   :
 * version: 1.0
 */

abstract class SddBaseViewModelActivity<Presenter : BaseViewModel> : BaseViewModelActivity<Presenter>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun initWidget() {
        super.initWidget()
        if (showBackNav()) {
            StatusBarHelper.initTitleBarUI(this, mTitleBar)
        }
    }
}