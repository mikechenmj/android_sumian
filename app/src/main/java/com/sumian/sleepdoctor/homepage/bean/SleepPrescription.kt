package com.sumian.sleepdoctor.homepage.bean

import com.google.gson.annotations.SerializedName

data class SleepPrescription(
        @SerializedName("id") val id: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("get_up_at") val getUpAt: String,
        @SerializedName("sleep_at") val sleepAt: String,
        @SerializedName("sleep_duration_avg") var sleepDurationAvg: Int,
        @SerializedName("sleep_duration_avg_suggested") val sleepDurationAvgSuggested: Int
)