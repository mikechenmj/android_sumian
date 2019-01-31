package com.sumian.sd.buz.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

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
        CaptchaHelper.requestCaptcha(mobile, object : CaptchaHelper.RequestCaptchaListener {
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

    override fun validatePhoneNumberForResetPassword(mobile: String, captcha: String) {
        view.showLoading()
        val call = AppManager.getSdHttpService().validateCaptchaForResetPassword(mobile, captcha)
        call.enqueue(object : BaseSdResponseCallback<Token>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: Token?) {
                SettingPasswordActivity.start(false, response?.token)
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
        val call = AppManager.getSdHttpService().bindSocial(map)
        call.enqueue(object : BaseSdResponseCallback<Token>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: Token?) {
                AppManager.onLoginSuccess(response)
                StatUtil.event("e_login_success", mapOf("mode" to "微信"))
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }
}