package com.sumian.hw.report.weeklyreport.bean

import com.google.gson.annotations.SerializedName

data class Meta(
        @SerializedName("earliest_week")
        val earliestWeek: Int
)