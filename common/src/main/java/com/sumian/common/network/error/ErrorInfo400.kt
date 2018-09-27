package com.sumian.common.network.error

import com.google.gson.annotations.SerializedName

data class ErrorInfo400(
        @SerializedName("message") val message: String,
        @SerializedName("status_code") val statusCode: Int
)