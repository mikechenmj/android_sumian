package com.sumian.sd.diary.sleeprecord.bean

import com.google.gson.annotations.SerializedName

data class SleepRecord(
        @SerializedName("created_at")
        val createdAt: Int, // 1544064571
        @SerializedName("date")
        val date: Int, // 1544025600
        @SerializedName("doctor_evaluation")
        val doctorEvaluation: String,
        @SerializedName("energetic")
        val energetic: Int, // 0
        @SerializedName("fall_asleep_duration")
        val fallAsleepDuration: Int, // 0
        @SerializedName("get_up_at")
        val getUpAt: Int, // 0
        @SerializedName("id")
        val id: Int, // 1244
        @SerializedName("on_bed_duration")
        val onBedDuration: Int, // 0
        @SerializedName("other_sleep_times")
        val otherSleepTimes: Int, // 0
        @SerializedName("other_sleep_total_minutes")
        val otherSleepTotalMinutes: Int, // 0
        @SerializedName("remark")
        val remark: String?, // null
        @SerializedName("sleep_at")
        val sleepAt: Int, // 0
        @SerializedName("sleep_cost")
        val sleepCost: Int, // 0
        @SerializedName("sleep_duration")
        val sleepDuration: Int, // 25200
        @SerializedName("sleep_efficiency")
        val sleepEfficiency: Int, // 88
        @SerializedName("sleep_pills")
        val sleepPills: List<SleepPill>?, // null
        @SerializedName("timezone")
        val timezone: Int, // 8
        @SerializedName("try_to_sleep_at")
        val tryToSleepAt: Int, // 0
        @SerializedName("updated_at")
        val updatedAt: Int, // 1544064571
        @SerializedName("wake_minutes")
        val wakeMinutes: Int, // 0
        @SerializedName("wake_seconds_avg")
        val wakeSecondsAvg: Int, // 0
        @SerializedName("wake_seconds_interval")
        val wakeSecondsInterval: Int, // 0
        @SerializedName("wake_times")
        val wakeTimes: Int, // 0
        @SerializedName("wake_up_at")
        val wakeUpAt: Int // 0
)