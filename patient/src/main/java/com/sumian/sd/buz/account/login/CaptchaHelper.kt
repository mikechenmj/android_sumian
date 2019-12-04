package com.sumian.sd.buz.account.login

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import retrofit2.Call
import java.lang.ref.SoftReference

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/16 16:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object CaptchaHelper {

    fun requestCaptcha(mobile: String, listener: RequestCaptchaListener): Call<Unit> {
        val listenerWf = SoftReference<RequestCaptchaListener>(listener)
        listener.onStart()
        val call = AppManager.getSdHttpService().getCaptcha(mobile)
        call.enqueue(object : BaseSdResponseCallback<Unit>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                listenerWf.get()?.onFail(errorResponse.code)
                if (errorResponse.code != 4001) {
                    ToastUtils.showShort(errorResponse.message)
                }
            }

            override fun onSuccess(response: Unit?) {
                ToastUtils.showShort(R.string.captcha_send_success)
                listenerWf.get()?.onSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                listenerWf.get()?.onFinish()
            }
        })
        return call
    }

    fun requestCaptcha(mobile: String, captchaId: String, captchaPhrase: String, listener: RequestCaptchaListener): Call<Unit> {
        val listenerWf = SoftReference<RequestCaptchaListener>(listener)
        listener.onStart()
        val call = AppManager.getSdHttpService().getCaptcha(mobile, captchaId, captchaPhrase)
        call.enqueue(object : BaseSdResponseCallback<Unit>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                listenerWf.get()?.onFail(errorResponse.code)
                if (errorResponse.code != 4001) {
                    ToastUtils.showShort(errorResponse.message)
                }
            }

            override fun onSuccess(response: Unit?) {
                ToastUtils.showShort(R.string.captcha_send_success)
                listenerWf.get()?.onSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                listenerWf.get()?.onFinish()
            }
        })
        return call
    }

    interface RequestCaptchaListener {
        fun onStart()
        fun onSuccess()
        fun onFail(code: Int) {}
        fun onFinish()
    }
}