package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.hw.utils.AppUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/15 16:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ValidatePhoneNumberPresenter(var view: ValidatePhoneNumberContract.View) : ValidatePhoneNumberContract.Presenter {
    override fun requestCaptcha(mobile: String) {
        LoginHelper.requestCaptcha(mobile, object : LoginHelper.RequestCaptchaListener {
            override fun onStart() {
                view.showLoading()
            }

            override fun onSuccess() {
                view.onRequestCaptchaSuccess()
            }

            override fun onFinish() {
                view.dismissLoading()
            }
        })
    }

    override fun validatePhoneNumberForRegister(mobile: String, captcha: String) {
        view.showLoading()
        val call = AppManager.getHttpService().loginByCaptcha(mobile, captcha)
        call.enqueue(object : BaseResponseCallback<Token>() {

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFailure(code: Int, message: String) {
                ToastUtils.showShort(message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun validatePhoneNumberForModifyPassword(mobile: String, captcha: String) {
        view.showLoading()
        val call = AppManager.getHttpService().loginByCaptcha(mobile, captcha)  // todo change loginByCaptcha
        call.enqueue(object : BaseResponseCallback<Token>() {

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFailure(code: Int, message: String) {
                ToastUtils.showShort(message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun bindMobile(mobile: String, captcha: String, socialInfo: String) {
        view.showLoading()
        val map = mutableMapOf<String, Any>()
        map["mobile"] = mobile
        map["captcha"] = captcha
        map["type"] = 0
        map["info"] = socialInfo
        val call = AppManager.getHttpService().bindSocial(map)
        call.enqueue(object : BaseResponseCallback<Token>() {
            override fun onSuccess(response: Token?) {
                if (response == null) {
                    ToastUtils.showShort(R.string.error)
                    return
                }
                AppManager.getAccountViewModel().updateToken(response)
                if (response.user.hasPassword) {
                    AppUtil.launchMainAndFinishAll()
                } else {
                    SetPasswordActivity.launch()
                }
            }

            override fun onFailure(code: Int, message: String) {
                ToastUtils.showShort(message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }
}