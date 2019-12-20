package com.sumian.sddoctor.login.login

import android.app.Activity
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.dialog.SumianImageTextToast
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA

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

    private var mIsVisitorAccount = false

    override fun loginByPassword(mobile: String, password: String) {
        mIsVisitorAccount = (BuildConfig.VISITOR_MOBILE == mobile)
        view.showLoading()
        AppManager.getHttpService().loginByPassword(mobile, password)
                .enqueue(object : BaseSdResponseCallback<LoginResponse>() {
                    override fun onSuccess(response: LoginResponse?) {
                        onLoginSuccess(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        ToastUtils.showShort(errorResponse.message)
                    }

                    override fun onFinish() {
                        view.dismissLoading()
                    }
                })
    }

    override fun loginByCaptcha(mobile: String, captcha: String) {
        view.showLoading()
        AppManager.getHttpService().loginByCaptcha(mobile, captcha)
                .enqueue(object : BaseSdResponseCallback<LoginResponse>() {
                    override fun onSuccess(response: LoginResponse?) {
                        onLoginSuccess(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        ToastUtils.showShort(errorResponse.message)
                    }

                    override fun onFinish() {
                        view.dismissLoading()
                    }
                })
    }

    override fun loginByWechat(activity: Activity) {
        view.showLoading()
        AppManager.getOpenLogin().weChatLogin(activity, object : UMAuthListener {
            override fun onStart(share_media: SHARE_MEDIA) {
                LogUtils.d()
            }

            override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: MutableMap<String, String>) {
                AppManager.getOpenLogin().deleteWechatTokenCache(activity, null)
                LogUtils.d(map)
                val unionId = map["unionid"] ?: ""
                val openId = map["openid"] ?: ""
//                val nickname = map["name"] ?: ""
                map["nickname"] = map["name"] ?: ""
                map["headimgurl"] = map["profile_image_url"] ?: ""
                AppManager.getHttpService().loginBySocialite(0, unionId, openId)
                        .enqueue(object : BaseSdResponseCallback<LoginResponse>() {
                            override fun onSuccess(response: LoginResponse?) {
                                onLoginSuccess(response)
                            }

                            override fun onFailure(errorResponse: ErrorResponse) {
                                LogUtils.d(errorResponse)
                                if (errorResponse.code == 1) {
                                    BindWechatActivity.launch(JsonUtil.toJson(map))
                                } else {
                                    ToastUtils.showShort(errorResponse.message)
                                }
                            }

                            override fun onFinish() {
                                view.dismissLoading()
                            }
                        })
            }

            override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
                LogUtils.d()
                view.dismissLoading()
                ToastUtils.showShort(throwable.message)
            }

            override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
                LogUtils.d()
                view.dismissLoading()
                ToastUtils.showShort(view.getContext().getString(R.string.wechat_login_canceled))
            }
        })
    }

    override fun requestCaptcha(mobile: String) {
        view.showLoading()
        AppManager.getHttpService().requestLoginCaptcha(mobile)
                .enqueue(object : BaseSdResponseCallback<Any>() {
                    override fun onSuccess(response: Any?) {
                        view.onRequestCaptchaSuccess()
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        if (errorResponse.code != 4001) {
                            ToastUtils.showShort(errorResponse.message)
                        }
                        view.onRequestCaptchaFail(errorResponse.code)
                    }

                    override fun onFinish() {
                        view.dismissLoading()
                    }
                })
    }

    fun onLoginSuccess(response: LoginResponse?) {
        StatUtil.event(StatConstants.on_login_success)
        SumianImageTextToast.showToast(App.getAppContext(), R.drawable.ic_dialog_success, "登录成功", false)
        AppManager.onLoginSuccess(response)
    }
}