package com.sumian.sleepdoctor.h5.bean

import com.google.gson.annotations.SerializedName

data class H5BaseResponse(
        @SerializedName("code") val code: Int,
        @SerializedName("message") val message: String?
) {
    fun isSuccess(): Boolean {
        return code == 0
    }
}