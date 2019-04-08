package com.sumian.common.network.error

import com.google.gson.annotations.SerializedName

/**
 * 服务器400 error
 */
data class ErrorInfo400(
        @SerializedName("message") val message: String,
        @SerializedName("status_code") val statusCode: Int
)