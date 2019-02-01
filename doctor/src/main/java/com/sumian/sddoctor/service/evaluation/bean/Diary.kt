package com.sumian.sddoctor.service.evaluation.bean

import com.google.gson.annotations.SerializedName

/**
 * 周评估日记
 */
data class Diary(
        @SerializedName("id") val id: Int,
        @SerializedName("date") val date: Int,
        @SerializedName("answer") val answer: Answer,
        @SerializedName("sleep_duration") val sleepDuration: Int,
        @SerializedName("fall_asleep_duration") val fallAsleepDuration: Int,
        @SerializedName("sleep_efficiency") val sleepEfficiency: Int,
        @SerializedName("doctor_evaluation") val doctorEvaluation: String,
        @SerializedName("wake_seconds_avg") val wakeSecondsAvg: Int,
        @SerializedName("wake_seconds_interval") val wakeSecondsInterval: Float
)