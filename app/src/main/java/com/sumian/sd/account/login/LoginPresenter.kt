package com.sumian.sd.account.login

import android.app.Activity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 20:04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LoginPresenter(var view: LoginContract.View) : LoginContract.Presenter {

    override fun loginByPassword(mobile: String, password: String) {
    }

    override fun loginByCaptcha(mobile: String, captcha: String) {

    }

    override fun loginByWechat(activity: Activity) {
        view.showLoading()

    }

    override fun requestCaptcha(mobile: String) {

    }

}