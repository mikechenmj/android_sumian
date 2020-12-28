package com.sumian.sd.examine.login.viewmodel

import androidx.lifecycle.ViewModel
import com.sumian.common.network.error.ErrorCode
import com.sumian.common.network.error.ErrorInfo400
import com.sumian.common.network.error.ErrorInfo499
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import okhttp3.ResponseBody
import retrofit2.Call

open class BaseExamineViewModel: ViewModel() {

    protected val mWorkTasks = mutableListOf<Call<*>>()

    override fun onCleared() {
        super.onCleared()
        mWorkTasks.forEach { v -> if (!v.isCanceled) v.cancel() }
    }

    fun getErrorResponseFromErrorBody(code: Int, errorBody: ResponseBody): ErrorResponse? {
        val errorJson = errorBody.string()
        return when (code) {
            ErrorCode.BUSINESS_ERROR -> {
                val errorInfo = JsonUtil.fromJson(errorJson, ErrorInfo499::class.java)
                ErrorResponse.createFromErrorInfo(errorInfo)
            }
            ErrorCode.FORBIDDEN -> {
                val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorInfo400::class.java)
                ErrorResponse(code = code, message = errorInfo400!!.message)
            }
            else -> {
                val errorInfo400 = JsonUtil.fromJson(errorJson, ErrorInfo400::class.java)
                ErrorResponse.createFromErrorInfo(errorInfo400)
            }
        }
    }
}