package com.sumian.sd.account.login

import android.app.Activity
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
import retrofit2.Callback
import java.util.*

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
        view.showLoading()
        val call = AppManager.getSdHttpService().loginByPassword(mobile, password)
        call.enqueue(object : BaseSdResponseCallback<Token>() {

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun loginByCaptcha(mobile: String, captcha: String) {
        view.showLoading()
        val call = AppManager.getSdHttpService().loginByCaptcha(mobile, captcha)
        call.enqueue(object : BaseSdResponseCallback<Token>(), Callback<Token> {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun loginByWechat(activity: Activity) {
        view.showLoading()
        AppManager.getOpenLogin().weChatLogin(activity, object : UMAuthListener {
            override fun onComplete(shareMedia: SHARE_MEDIA?, p1: Int, map: MutableMap<String, String?>?) {
                checkOpenIsBind(map!!)
                view.dismissLoading()
            }

            override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                ToastUtils.showShort(R.string.login_cancel)
                view.dismissLoading()
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                ToastUtils.showShort(R.string.no_have_wechat)
                view.dismissLoading()
            }

            override fun onStart(p0: SHARE_MEDIA?) {
                ToastUtils.showShort(R.string.opening_wechat)
            }
        })
    }

    private fun checkOpenIsBind(openMap: MutableMap<String, String?>) {
        view.showLoading()
        val map = HashMap<String, Any?>()
        map["type"] = 0
        map["union_id"] = openMap["unionid"]
        val call = AppManager.getSdHttpService().loginOpenPlatform(map)
        call.enqueue(object : BaseSdResponseCallback<Token>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                if (errorResponse.code == 404) {
                    openMap["nickname"] = openMap["screen_name"]
                    openMap["headimgurl"] = openMap["profile_image_url"]
                    val socialInfo = JsonUtil.toJson(openMap)
                    ValidatePhoneNumberActivity.launchForBindMobile(socialInfo)
                } else {
                    ToastUtils.showShort(errorResponse.message)
                }
            }

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

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

}