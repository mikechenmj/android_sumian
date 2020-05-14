package com.sumian.sd.buz.account.login

import com.sumian.common.base.BaseShowLoadingView

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
        fun onRequestCaptchaFail(code: Int)
        fun onValidateSuccess()
        fun onValidateFailure()
        fun onRequestCaptchaSuccess()
        fun onMobileRebind(mobile: String, captcha: String, socialInfo: String, rebind: Boolean)
    }

}