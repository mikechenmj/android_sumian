package com.sumian.common.base

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc:
 *
 */
abstract class BaseViewModelFragment<VM : BaseViewModel> : BaseFragment() {

    protected var mViewModel: VM? = null

    override fun onRelease() {
        super.onRelease()
        mViewModel?.onCleared()
    }
}