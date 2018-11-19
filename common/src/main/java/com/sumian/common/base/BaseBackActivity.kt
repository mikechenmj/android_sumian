package com.sumian.common.base

import android.support.annotation.LayoutRes
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
@Deprecated("override showNavBar() instead")
abstract class BaseBackActivity : BasePresenterActivity<IPresenter>() {

    override fun showBackNav(): Boolean {
        return true
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return getChildContentId()
    }

    @LayoutRes
    protected abstract fun getChildContentId(): Int

}
