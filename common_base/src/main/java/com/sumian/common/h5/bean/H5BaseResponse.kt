package com.sumian.common.h5.bean

import com.google.gson.annotations.SerializedName

data class H5BaseResponse<T>(
        @SerializedName("code") val code: Int,
        @SerializedName("message") val message: String?,
        @SerializedName("result") val result: T?
) {
    fun isSuccess(): Boolean {
        return code == 0
    }
}