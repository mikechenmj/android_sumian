package com.sumian.common.container.mvp

import com.sumian.common.container.delegate.ContainerDelegate
import retrofit2.Call

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc:
 *
 */
abstract class BasePresenter<View : IView> : IPresenter {

    protected var mView: View? = null

    private val mContainerDelegate by lazy {
        ContainerDelegate.install()
    }

    protected fun addCall(call: Call<*>) {
        mContainerDelegate.addCall(call)
    }

    override fun onCancel() {
        mContainerDelegate.onCancel()
    }

    override fun onRelease() {
        onCancel()
        mContainerDelegate.onRelease()
        mView = null
    }
}