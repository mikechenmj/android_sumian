package com.sumian.sddoctor.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.sumian.common.base.BaseActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 16:09
 * desc   :
 * version: 1.0
 */

abstract class SddBaseActivity : BaseActivity() {
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