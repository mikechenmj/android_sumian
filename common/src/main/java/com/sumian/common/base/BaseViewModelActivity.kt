package com.sumian.common.base

/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
abstract class BaseViewModelActivity<VM : BaseViewModel> : BaseActivity(), BaseShowLoadingView {

    protected var mPresenter: VM? = null

    override fun onRelease() {
        super.onRelease()
        mPresenter?.onCleared()
    }

}