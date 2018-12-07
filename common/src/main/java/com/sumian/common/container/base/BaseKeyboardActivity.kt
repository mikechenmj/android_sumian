package com.sumian.common.container.base

import android.view.ViewGroup
import com.blankj.utilcode.util.KeyboardUtils
import com.sumian.common.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/12/6
 *
 * desc:可导航/可关闭软键盘的 activity container
 *
 */
abstract class BaseKeyboardActivity<Presenter : IPresenter> : BaseNavBackActivity<Presenter>() {

    override fun initWidget() {
        super.initWidget()
        registerCanCloseKeyboard()
    }

    private fun registerCanCloseKeyboard() {
        findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
            ?.setOnClickListener { KeyboardUtils.hideSoftInput(this@BaseKeyboardActivity) }
    }
}