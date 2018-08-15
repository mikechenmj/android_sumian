package com.sumian.sd.homepage.bean

import com.google.gson.annotations.SerializedName


data class UpdateSleepPrescriptionWhenFatiguedData(
        @SerializedName("id") val id: Int,
        @SerializedName("get_up_at") val getUpAt: String,
        @SerializedName("sleep_at") val sleepAt: String,
        @SerializedName("sleep_duration_avg") val sleepDurationAvg: Int,
        @SerializedName("add_sleep_duration") val addSleepDuration: Boolean
) {
    companion object {
        fun create(sleepPrescription: SleepPrescription, addSleepDuration: Boolean): UpdateSleepPrescriptionWhenFatiguedData {
            return UpdateSleepPrescriptionWhenFatiguedData(sleepPrescription.id, sleepPrescription.getUpAt, sleepPrescription.sleepAt, sleepPrescription.sleepDurationAvg, addSleepDuration)
        }
    }
}