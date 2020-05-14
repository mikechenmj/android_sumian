package com.sumian.common.network.response

import android.util.Log
import com.sumian.common.network.error.ErrorCode
import com.sumian.common.network.error.ErrorCode.BUSINESS_ERROR
import com.sumian.common.network.error.ErrorCode.FORBIDDEN
import com.sumian.common.network.error.ErrorInfo400
import com.sumian.common.network.error.ErrorInfo499
import com.sumian.common.utils.JsonUtil
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
abstract class BaseResponseCallback<Data> : Callback<Data> {

    companion object {

        private val TAG = BaseResponseCallback::class.java.simpleName

        private val UNKNOWN_ERROR_RESPONSE: ErrorResponse by lazy {
            ErrorResponse(0, "Error unknown")
        }

    }

    override fun onFailure(call: Call<Data>?, t: Throwable?) {
        onFinish()
        Log.d(TAG, t?.message)
        t?.let {
            it.printStackTrace()
            if ((it.message == "Socket closed" || it.message == "Canceled")) {
                return
            }
            onFailure(ErrorResponse(0, "网络异常，请检查您的网络情况"))
        }
    }

    override fun onResponse(call: Call<Data>?, response: Response<Data>?) {
        onFinish()
        if (response != null && response.isSuccessful) {
            val body = response.body()
            onSuccess(body)
        } else {
            val errorBody = response?.errorBody()
            if (errorBody == null) {
                onFailure(UNKNOWN_ERROR_RESPONSE)
                return
            }
            try {
                val errorResponse = getErrorResponseFromErrorBody(response.code(), errorBody)
                if (errorResponse == null) {
                    onFailure(UNKNOWN_ERROR_RESPONSE)
                } else {
                    errorResponse.response = response
                    onFailure(errorResponse)
                    when (errorResponse.code) {
                        //token 鉴权失败
                        ErrorCode.UNAUTHORIZED -> onUnauthorized()
                        ErrorCode.SERVICE_UNAVAILABLE -> showSystemIsMaintainDialog()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                onFailure(UNKNOWN_ERROR_RESPONSE)
            }
        }
    }

    private fun getErrorResponseFromErrorBody(code: Int, errorBody: ResponseBody): ErrorResponse? {
        val errorJson = errorBody.string()
        return when (code) {
            BUSINESS_ERROR -> {
                val errorInfo = JsonUtil.fromJson(errorJson, ErrorInfo499::class.java)
                ErrorResponse.createFromErrorInfo(errorInfo)
            }
            FORBIDDEN -> {
                val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorInfo400::class.java)
                ErrorResponse(code = code, message = errorInfo400!!.message)
            }
            else -> {
                val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorInfo400::class.java)
                ErrorResponse.createFromErrorInfo(errorInfo400)
            }
        }
    }

    protected abstract fun onSuccess(response: Data?)

    protected abstract fun onFailure(errorResponse: ErrorResponse)

    protected abstract fun onUnauthorized()

    protected open fun onFinish() {

    }

    protected open fun showSystemIsMaintainDialog() {
        Log.e("TAG", "showSystemIsMaintainDialog()")
    }

}