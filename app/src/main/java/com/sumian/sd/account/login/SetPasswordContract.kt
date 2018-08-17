package com.sumian.sd.account.login

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/15 16:14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SetPasswordContract {
    interface View : BaseShowLoadingView {
        fun onSetPasswordSuccess(data: String)
        fun onSetPasswordFailed(msg: String)
    }

    interface Presenter : IPresenter {
        fun setPassword(password: String)
    }
}