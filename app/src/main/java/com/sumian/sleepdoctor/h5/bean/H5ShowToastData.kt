package com.sumian.sleepdoctor.h5.bean

import com.google.gson.annotations.SerializedName

data class H5ShowToastData(
        @SerializedName("type") val type: String,
        @SerializedName("message") val message: String,
        @SerializedName("delay") val delay: Int,
        @SerializedName("duration") val duration: Int
)