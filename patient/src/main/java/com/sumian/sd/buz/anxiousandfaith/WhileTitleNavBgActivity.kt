package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R

/**
 * @author : chenmj
 * e-mail : 448900450@qq.com
 * time   : 2019/09/09 15:40
 * desc   :
 * version: 1.0
 */
abstract class WhileTitleNavBgActivity : BaseActivity() {

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setBgColor(Color.WHITE)
        mTitleBar.setTvAndIvColor(Color.BLACK)
        mTitleBar.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        mTitleBar.title.paint.isFakeBoldText = true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initWidget()
    }
}