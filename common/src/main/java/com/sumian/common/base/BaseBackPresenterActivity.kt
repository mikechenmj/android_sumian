package com.sumian.common.base

import com.sumian.common.mvp.IPresenter

/**
 * Created by sm
 *
 *
 * on 2018/8/1
 *
 *
 * desc:
 */
@Suppress("DEPRECATION")
@Deprecated("use BaseActivity and override showNavBar")
abstract class BaseBackPresenterActivity<Presenter : IPresenter> : BaseBackActivity() {
}
