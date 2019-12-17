package com.sumian.sddoctor.login.login

import android.app.Activity
import android.content.Context
import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView

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

    interface View : BaseView {
        fun launchMain()
        fun onRequestCaptchaSuccess()
        fun getContext(): Context
        fun onRequestCaptchaFail(code: Int)
    }

    interface Presenter : BasePresenter {
        fun loginByPassword(mobile: String, password: String)
        fun loginByCaptcha(mobile: String, captcha: String)
        fun loginByWechat(activity: Activity)
        fun requestCaptcha(mobile: String)
        fun requestCaptcha(mobile: String, captchaId: String, captchaPhrase: String)
    }
}