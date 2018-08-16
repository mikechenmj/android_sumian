package com.sumian.sd.account.login

import com.alibaba.sdk.android.oss.model.ImagePersistRequest
import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/15 16:01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ValidatePhoneNumberContract {
    interface View : BaseShowLoadingView {
        fun onValidateSuccess()
        fun onValidateFailure()
    }

    interface Presenter : IPresenter {
        fun requestCaptcha(mobile: String)
        fun validatePhoneNumber(mobile: String, captcha: String)
    }
}