package com.sumian.common.container.mvp

import com.sumian.common.container.delegate.ContainerDelegate
import com.sumian.common.mvp.IView
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

    override fun addCall(call: Call<*>) {
        mContainerDelegate.addCallAction(call)
    }

    override fun onRelease() {
        mContainerDelegate.onCancel()
        mContainerDelegate.onRelease()
        mView = null
    }
}