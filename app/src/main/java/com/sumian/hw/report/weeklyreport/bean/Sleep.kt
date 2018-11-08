package com.sumian.hw.report.weeklyreport.bean

import com.google.gson.annotations.SerializedName

data class Sleep(
        @SerializedName("awake_duration")
        val awakeDuration: Int,
        @SerializedName("deep_duration")
        val deepDuration: Int,
        @SerializedName("light_duration")
        val lightDuration: Int,
        @SerializedName("sleep_duration")
        val sleepDuration: Int
)