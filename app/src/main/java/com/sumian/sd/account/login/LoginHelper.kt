package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.AppUtil
import java.lang.ref.WeakReference

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/16 16:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LoginHelper {

    companion object {
        fun onLoginSuccess(token: Token?) {
            if (token == null) {
                ToastUtils.showShort(R.string.error)
                return
            }
            updateTokenAndUploadInstallationId(token)
            AppUtil.launchMainOrNewUserGuide()
        }

        fun updateTokenAndUploadInstallationId(response: Token?) {
            AppManager.getAccountViewModel().updateToken(response)
        }

        fun requestCaptcha(mobile: String, listener: RequestCaptchaListener) {
            val listenerWf = WeakReference<RequestCaptchaListener>(listener)
            listener.onStart()
            val call = AppManager.getSdHttpService().getCaptcha(mobile)
            call.enqueue(object : BaseSdResponseCallback<Unit>() {
                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                    listenerWf.get()?.onSuccess()
                }

                override fun onSuccess(response: Unit?) {
                    ToastUtils.showShort(R.string.captcha_send_success)
                }

                override fun onFinish() {
                    super.onFinish()
                    listenerWf.get()?.onFinish()
                }
            })
        }
    }

    interface RequestCaptchaListener {
        fun onStart()
        fun onSuccess()
        fun onFinish()
    }
}