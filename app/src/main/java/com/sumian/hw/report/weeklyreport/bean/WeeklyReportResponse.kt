package com.sumian.hw.report.weeklyreport.bean

import com.google.gson.annotations.SerializedName

data class WeeklyReportResponse(
        @SerializedName("data")
        val list: List<SleepDurationReport>,
        @SerializedName("meta")
        val meta: Meta
)