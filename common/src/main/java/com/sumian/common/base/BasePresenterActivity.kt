package com.sumian.common.base

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
abstract class BasePresenterActivity<Presenter : IPresenter> : BaseActivity(), BaseShowLoadingView {

    protected var mPresenter: Presenter? = null

    override fun onRelease() {
        super.onRelease()
        mPresenter?.onRelease()
    }

}