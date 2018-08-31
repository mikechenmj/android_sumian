package com.sumian.common.base

import com.sumian.common.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc:
 *
 */
abstract class BasePresenterFragment<Presenter : IPresenter> : BaseFragment() {

    protected var mPresenter: Presenter? = null

    override fun onRelease() {
        super.onRelease()
        mPresenter?.onRelease()
    }
}