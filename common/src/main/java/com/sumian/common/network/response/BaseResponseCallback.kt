package com.sumian.common.network.response

import android.util.Log
import com.sumian.common.network.StatusCode.BUSINESS_ERROR
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
                    onFailure(errorResponse)
                    when (errorResponse.code) {
                        //token 鉴权失败
                        401 -> showTokenInvalidState()
                        503 -> showSystemIsMaintainDialog()
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
        return if (code == BUSINESS_ERROR) {
            val errorInfo = JsonUtil.fromJson(errorJson, ErrorResponse.ErrorInfo499::class.java)
            ErrorResponse.createFromErrorInfo(errorInfo)
        } else {
            val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorResponse.ErrorInfo400::class.java)
            ErrorResponse.createFromErrorInfo(errorInfo400)
        }
    }

    protected abstract fun onSuccess(response: Data?)

    protected abstract fun onFailure(errorResponse: ErrorResponse)

    protected open fun onFinish() {

    }

    protected open fun showTokenInvalidState() {
        Log.e("TAG", "showTokenInvalidState()")
    }

    protected open fun showSystemIsMaintainDialog() {
        Log.e("TAG", "showSystemIsMaintainDialog()")
    }

}