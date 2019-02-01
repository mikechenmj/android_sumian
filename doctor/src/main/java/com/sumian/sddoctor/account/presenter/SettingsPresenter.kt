@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.sumian.sddoctor.account.presenter

import android.app.Activity
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.contract.SettingsContract
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.login.login.bean.SocialiteInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.JsonUtil
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * <pre>
 *     @author : sm
 *     @e-mail : yaoqi.y@sumian.com
 *     @time   : 2018/6/25 16:54
 *
 *     @version: 1.0
 *
 *     @desc   :
 *
 * </pre>
 */
class SettingsPresenter private constructor(view: SettingsContract.View) : BaseViewModel() {

    private var mView: SettingsContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: SettingsContract.View): SettingsPresenter {
            return SettingsPresenter(view)
        }
    }

    fun unbindWechat() {

        mView?.showLoading()

        var socialId = 0
        AppManager.getAccountViewModel().getDoctorInfo().value?.let {
            it.socialite.let {
                it.forEach { socialiteInfo ->
                    if (socialiteInfo.type == 0) {
                        socialId = socialiteInfo.id
                    }
                }
            }
        }

        val unbindWechat = AppManager.getHttpService().unbindSocialite(socialId)
        unbindWechat.enqueue(object : BaseSdResponseCallback<Any>() {

            override fun onSuccess(response: Any?) {
                val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
                doctorInfo?.let {
                    it.socialite.apply {
                        this.forEachIndexed { index, socialiteInfo ->
                            if (socialiteInfo.type == 0) {
                                this.removeAt(index)
                            }
                        }
                    }
                }
                AppManager.getAccountViewModel().updateDoctorInfo(doctorInfo)
                mView?.onUnbindSuccess()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onUnBindFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }

    fun bindWechat(activity: Activity) {

        mView?.showLoading()

        AppManager.getOpenLogin().weChatLogin(activity, object : UMAuthListener {

            override fun onStart(share_media: SHARE_MEDIA) {
                LogUtils.d()
            }

            override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: MutableMap<String, String>) {
                LogUtils.d(map)
                AppManager.getOpenLogin().deleteWechatTokenCache(activity, null)
                map["nickname"] = map.getValue("name")
                val toJson = JsonUtil.toJson(map)
                bindWechat(toJson)
            }

            override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
                mView?.onBindFailed(App.getAppContext().getString(R.string.not_installed_wechat))
                mView?.dismissLoading()
            }

            override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
                mView?.onCancelBind(App.getAppContext().getString(R.string.wechat_login_canceled))
                mView?.dismissLoading()
            }
        })
    }

    private fun bindWechat(toJson: String) {
        AppManager.getHttpService().bindSocialite(toJson).enqueue(object : BaseSdResponseCallback<SocialiteInfo>() {

            override fun onSuccess(response: SocialiteInfo?) {
                response?.let {
                    val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value
                    doctorInfo?.let {
                        val socialite = it.socialite
                        if (socialite.isEmpty()) {
                            socialite.add(response)
                        } else {
                            socialite.forEachIndexed { index, socialiteInfo ->
                                if (socialiteInfo.type == 0) {
                                    it.socialite[index] = response
                                }
                            }
                        }
                        doctorInfo.socialite = socialite
                        AppManager.getAccountViewModel().updateDoctorInfo(doctorInfo, true)
                    }
                }
                mView?.onBindSuccess()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onBindFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }
        })
    }
}