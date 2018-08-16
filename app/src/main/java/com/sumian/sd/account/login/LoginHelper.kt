package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.hw.utils.AppUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.leancloud.LeanCloudManager
import com.sumian.sd.network.callback.BaseResponseCallback
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
        fun onLoginSuccess(response: Token?) {
            if (response == null) {
                ToastUtils.showShort(R.string.error)
                return
            }
            updateTokenAndUploadInstallationId(response)
            if (response.user.hasPassword) {
                AppUtil.launchMainAndFinishAll()
            } else {
                SetPasswordActivity.launch()
            }
        }

        fun updateTokenAndUploadInstallationId(response: Token?) {
            AppManager.getAccountViewModel().updateToken(response)
            LeanCloudManager.getAndUploadCurrentInstallation()
        }

        fun requestCaptcha(mobile: String, listener: RequestCaptchaListener) {
            val listenerWf = WeakReference<RequestCaptchaListener>(listener)
            listener.onStart()
            val call = AppManager.getHttpService().getCaptcha(mobile)
            call.enqueue(object : BaseResponseCallback<Unit>() {
                override fun onSuccess(response: Unit?) {
                    ToastUtils.showShort(R.string.captcha_send_success)
                }

                override fun onFailure(code: Int, message: String) {
                    ToastUtils.showShort(message)
                    listenerWf.get()?.onSuccess()
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