package com.sumian.sd.homepage.bean

import com.google.gson.annotations.SerializedName

data class SleepPrescriptionStatus(
        val data: List<SleepPrescriptionData>,
        val meta: Meta
)

data class SleepPrescriptionData(
        val id: Int,
        val date: Int
)

data class Meta(
        val real_sleep_duration_avg: Int,
        val stop_service: Boolean,
        val prescription: Prescription,
        val enquire: Boolean,
        val update: Boolean,
        val keep: Boolean
)

data class Prescription(
        val status: Boolean,
        val data: SleepPrescription?,
        val sleep_duration_avg_new: Any,
        val days_remain:Int

)

data class SleepPrescription(
        @SerializedName("id") val id: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("get_up_at") val getUpAt: String,
        @SerializedName("sleep_at") val sleepAt: String,
        @SerializedName("sleep_duration_avg") var sleepDurationAvg: Int
)