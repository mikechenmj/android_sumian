package com.sumian.sleepdoctor.h5.bean

import com.google.gson.annotations.SerializedName
import com.sumian.sleepdoctor.utils.JsonUtil

data class H5ShowToastData(
        @SerializedName("type") var type: String = "text",   //1.text 2.success 3.error 4.loading 5.warning
        @SerializedName("message") var message: String = "",
        @SerializedName("delay") var delay: Long = 0,
        @SerializedName("duration") var duration: Long = 0
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