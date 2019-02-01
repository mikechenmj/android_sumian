package com.sumian.sddoctor.service.evaluation.bean

import com.google.gson.annotations.SerializedName

/**
 * 患者在填写日记时对提问的回答
 */
data class Answer(
        @SerializedName("bed_at") val bedAt: String,
        @SerializedName("try_to_sleep_at") val tryToSleepAt: String,
        @SerializedName("sleep_cost") val sleepCost: String,
        @SerializedName("wake_up_at") val wakeUpAt: String,
        @SerializedName("get_up_at") val getUpAt: String,
        @SerializedName("wake_times") val wakeTimes: Int,
        @SerializedName("wake_minutes") val wakeMinutes: Int,
        @SerializedName("energetic") val energetic: Int,
        @SerializedName("sleepless_factor") val sleeplessFactor: List<String>,
        @SerializedName("other_sleep_times") val otherSleepTimes: Int,
        @SerializedName("other_sleep_total_minutes") val otherSleepTotalMinutes: Int,
        @SerializedName("sleep_pills") val sleepPills: List<Any>,
        @SerializedName("remark") val remark: String,
        @SerializedName("bed_at_timestamp") val bedAtTimestamp: Int,
        @SerializedName("try_to_sleep_at_timestamp") val tryToSleepAtTimestamp: Int,
        @SerializedName("sleep_cost_minutes") val sleepCostMinutes: Int,
        @SerializedName("wake_up_timestamp") val wakeUpTimestamp: Int,
        @SerializedName("get_up_timestamp") val getUpTimestamp: Int
)