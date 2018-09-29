package com.sumian.common.network.error

import com.google.gson.annotations.SerializedName

/**
 * 服务器499 error
 */
data class ErrorInfo499(
        @SerializedName("error") val error: Error
)