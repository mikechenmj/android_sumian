package com.sumian.sd.account.login

import android.app.Activity
import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

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

    interface Presenter : IPresenter {
        fun loginByPassword(mobile: String, password: String)
        fun loginByCaptcha(mobile: String, captcha: String)
        fun loginByWechat(activity: Activity)
        fun requestCaptcha(mobile: String)
    }
}