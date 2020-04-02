package com.sumian.sd.buz.huaweihealth

import com.google.gson.annotations.SerializedName

data class HuaweiHealthConfigInfo(
        val id: Int,
        @SerializedName("max_date_duration")
        val maxDateDuration:Int,
        @SerializedName("created_at")
        val createdAt:Long,
        @SerializedName("updated_at")
        val updatedAt:Long,
        @SerializedName("latest_time")
        val latestTime:Long,
        @SerializedName("start_date")
        val startDate:Long,
        @SerializedName("end_date")
        val endDate:Long
        )
