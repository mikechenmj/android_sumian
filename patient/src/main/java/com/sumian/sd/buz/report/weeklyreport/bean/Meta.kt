package com.sumian.sd.buz.report.weeklyreport.bean

import com.google.gson.annotations.SerializedName

data class Meta(
        @SerializedName("earliest_week")
        val earliestWeek: Int
)