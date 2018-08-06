package com.sumian.sleepdoctor.network.callback

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.hw.network.callback.ErrorCode
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.network.StatusCode
import com.sumian.sleepdoctor.network.response.ErrorInfo400
import com.sumian.sleepdoctor.network.response.ErrorInfo499
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 10:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseResponseCallback<T> : Callback<T> {

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        onFinish()
        if (response != null && response.isSuccessful) {
            val body = response.body()
            onSuccess(body)
        } else {
            val errorBody = response?.errorBody()
            if (errorBody == null) {
                unknownError()
                return
            }
            try {
                val errorResponse = getErrorResponseFromErrorBody(response.code(), errorBody)
                if (errorResponse == null) {
                    unknownError()
                } else {
                    onFailure(errorResponse.code, errorResponse.message)
                    when (errorResponse.code) {
                        //token 鉴权失败
                        401 -> AppManager.getAccountViewModel().updateTokenInvalidState(true)
                        503 -> showSystemIsMaintainDialog()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                unknownError()
            }
        }
    }

    private fun unknownError() {
        onFailure(ErrorCode.UNKNOWN, "Error unknown")
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        onFinish()
        t?.let {

            it.printStackTrace()
            if ((it.message === "Socket closed" || it.message === "Canceled")) {
                return
            }
            onFailure(0, it.message
                    ?: App.getAppContext().getString(R.string.error_request_failed_hint))
        }
    }

    private fun getErrorResponseFromErrorBody(code: Int, errorBody: ResponseBody): ErrorResponse? {
        val errorJson = errorBody.string()
        return if (code == StatusCode.BUSINESS_ERROR) {
            val errorInfo = JsonUtil.fromJson(errorJson, ErrorInfo499::class.java)
            ErrorResponse.createFromErrorInfo(errorInfo)
        } else {
            val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorInfo400::class.java)
            ErrorResponse.createFromErrorInfo(errorInfo400)
        }
    }

    private fun showSystemIsMaintainDialog() {
        SumianAlertDialog(ActivityUtils.getTopActivity())
                .setTitle(R.string.system_maintain)
                .setMessage(R.string.system_maintain_desc)
                .setRightBtn(R.string.confirm, null)
                .show()
    }

    protected abstract fun onSuccess(response: T?)

    protected fun onFailure(errorResponse: ErrorResponse) {

    }

    protected abstract fun onFailure(code: Int, message: String)

    protected open fun onFinish() {}

}