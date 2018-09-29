package com.sumian.common.network.response

import com.sumian.common.network.error.ErrorInfo400
import com.sumian.common.network.error.ErrorInfo499

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
        @JvmStatic
        fun createFromErrorInfo(errorInfo: ErrorInfo499?): ErrorResponse? {
            if (errorInfo == null) {
                return null
            }
            return ErrorResponse(errorInfo.error.code, errorInfo.error.userMessage)
        }

        @JvmStatic
        fun createFromErrorInfo(errorInfo: ErrorInfo400?): ErrorResponse? {
            if (errorInfo == null) {
                return null
            }
            return ErrorResponse(errorInfo.statusCode, errorInfo.message)
        }
    }
}





