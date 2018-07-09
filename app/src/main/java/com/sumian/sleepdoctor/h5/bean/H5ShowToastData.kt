package com.sumian.sleepdoctor.h5.bean

import com.google.gson.annotations.SerializedName
import com.sumian.sleepdoctor.utils.JsonUtil

data class H5ShowToastData(
        @SerializedName("type") var type: String = "loading",
        @SerializedName("message") var message: String = "",
        @SerializedName("delay") var delay: Int = 0,
        @SerializedName("duration") var duration: Int = 0
) {
    companion object {
        fun fromJson(json: String?): H5ShowToastData {
            var toastData = JsonUtil.fromJson(json, H5ShowToastData::class.java)
            if (toastData == null) {
                toastData = H5ShowToastData()
            }
            return toastData
        }
    }
}