package com.sumian.sddoctor.base

import android.os.Looper
import android.view.Gravity
import androidx.annotation.StringRes
import com.sumian.common.helper.ToastHelper
import com.sumian.sddoctor.util.SumianExecutor.Companion.runOnUiThread

abstract class BasePresenterFragment<Presenter : BasePresenter> : BaseFragment() {

    protected var mPresenter: Presenter? = null

    protected fun showToast(message: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastHelper.show(message)
        } else {
            runOnUiThread { ToastHelper.show(message) }
        }
    }

    protected fun showToast(@StringRes messageId: Int) {
        showToast(getString(messageId))
    }

    protected fun showCenterToast(message: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread { ToastHelper.show(context, message, Gravity.CENTER) }
        } else {
            ToastHelper.show(context, message, Gravity.CENTER)
        }
    }

    protected fun showCenterToast(@StringRes messageId: Int) {
        showCenterToast(getString(messageId))
    }

    protected fun runOnUiThread(run: () -> Unit) {
        runOnUiThread(run, 0)
    }

    protected fun runOnUiThread(run: Runnable, delay: Long) {
        this.mRoot?.postDelayed(run, delay)
    }

}