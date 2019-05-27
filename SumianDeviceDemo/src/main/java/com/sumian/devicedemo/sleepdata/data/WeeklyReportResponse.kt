package com.sumian.devicedemo.sleepdata.data

import com.google.gson.annotations.SerializedName

data class WeeklyReportResponse(
        @SerializedName("data")
        val list: List<SleepDurationReport>,
        @SerializedName("meta")
        val meta: Meta
)

data class Meta(
        @SerializedName("earliest_week")
        val earliestWeek: Int
)