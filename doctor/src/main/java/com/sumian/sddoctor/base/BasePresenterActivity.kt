package com.sumian.sddoctor.base

import android.app.Activity
import android.view.Gravity
import com.sumian.common.helper.ToastHelper

/**
 * @see SddBaseActivity
 */
@Deprecated("see #SddBaseActivity")
abstract class BasePresenterActivity<Presenter : BasePresenter> : BaseActivity() {

    protected var mPresenter: Presenter? = null


    fun Activity.showCenterToast(text: String) {
        ToastHelper.show(this, text, Gravity.CENTER)
    }

}