package com.sumian.common.network.error

import com.google.gson.annotations.SerializedName

/**
 * 服务器错误 Response
 */
data class Error(
        @SerializedName("code") val code: Int,
        @SerializedName("user_message") val userMessage: String,
        @SerializedName("internal_message") val internalMessage: String,
        @SerializedName("more_info") val moreInfo: Any
)