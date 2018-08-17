package com.sumian.sd.account.login

import android.app.Activity
import com.blankj.utilcode.util.ToastUtils
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.login.LoginHelper.Companion.onLoginSuccess
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
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
        val call = AppManager.getHttpService().loginByPassword(mobile, password)
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

    override fun loginByCaptcha(mobile: String, captcha: String) {
        view.showLoading()
        val call = AppManager.getHttpService().loginByCaptcha(mobile, captcha)
        call.enqueue(object : BaseResponseCallback<Token>() {

            override fun onSuccess(response: Token?) {
                onLoginSuccess(response)
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
        val call = AppManager.getHttpService().loginOpenPlatform(map)
        call.enqueue(object : BaseResponseCallback<Token>() {
            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
            }

            override fun onFailure(code: Int, message: String) {
                if (code == 404) {
                    openMap["nickname"] = openMap["screen_name"]
                    val socialInfo = JsonUtil.toJson(openMap)
                    ValidatePhoneNumberActivity.launchForBindMobile(socialInfo)
                } else {
                    ToastUtils.showShort(message)
                }
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