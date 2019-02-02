package com.sumian.sd.buz.account.login

import com.sumian.common.mvp.BaseShowLoadingView

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 20:01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LoginContract {

    interface View : BaseShowLoadingView {
        fun onRequestCaptchaSuccess()
    }

}