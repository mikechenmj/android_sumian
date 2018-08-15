package com.sumian.sd.network.response

import com.google.gson.annotations.SerializedName

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 10:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class ErrorResponse(var code: Int, var message: String) {
    companion object {

        const val STATUS_CODE_ERROR_UNKNOWN = 0
        const val STATUS_CODE_NOT_FOUND = 404

        fun createFromErrorInfo(errorInfo: ErrorInfo499?): ErrorResponse? {
            return errorInfo?.let { ErrorResponse(errorInfo.error.code, errorInfo.error.userMessage) }
        }

        fun createFromErrorInfo(errorInfo: ErrorInfo400?): ErrorResponse? {
            return errorInfo?.let { ErrorResponse(errorInfo.statusCode, errorInfo.message) }
        }
    }

    fun isNotFound(): Boolean {
        return code == STATUS_CODE_NOT_FOUND
    }
}

data class ErrorInfo499(
        @SerializedName("error") val error: Error
)

data class Error(
        @SerializedName("code") val code: Int,
        @SerializedName("user_message") val userMessage: String,
        @SerializedName("internal_message") val internalMessage: String,
        @SerializedName("more_info") val moreInfo: Any
)

data class ErrorInfo400(
        @SerializedName("message") val message: String,
        @SerializedName("status_code") val statusCode: Int
)