package com.sumian.sd.buz.huaweihealth

import com.google.gson.annotations.SerializedName

data class HuaweiHealthData(var gender: Int = 3,
                            var birthday: String = "",
                            @SerializedName("heights")
                            var height: Float = 0f,
                            @SerializedName("weights")
                            var weight: Float = 0f,
                            @SerializedName("core_sleeps")
                            var coreSleeps: ArrayList<CoreSleep> = ArrayList(),
                            @SerializedName("step_sum")
                            var stepSum: ArrayList<StepSum> = ArrayList(),
                            @SerializedName("distance_sum")
                            var distanceSum: ArrayList<DistanceSum> = ArrayList(),
                            @SerializedName("calories_sum")
                            var caloriesSum: ArrayList<CaloriesSum> = ArrayList()) {

    data class CoreSleep(
            /**44101**/
            @SerializedName("rem_duration") var remSleepDuration: Float = 0f,
            /**44102**/
            @SerializedName("deep_sleep_duration") var deepSleepDuration: Float = 0f,
            /**44103**/
            @SerializedName("shallow_sleep_duration") var lightSleepDuration: Float = 0f,
            /**44105**/
            @SerializedName("total_sleep_duration") var totalSleepDuration: Float = 0f,
            /**44201**/
            @SerializedName("sleep_at") var fallAsleepTime: Long = 0,
            /**44202**/
            @SerializedName("awake_at") var wakeUpTime: Long = 0,
            /**44106**/
            @SerializedName("deep_sleep_continuity_score") var deepSleepContinuityScore: Float = 0f,
            /**44107**/
            @SerializedName("awake_times") var numberOfSleepWakefulness: Float = 0f,
            /**44203**/
            @SerializedName("sleep_score") var sleepScore: Float = 0f,
            /**44105 - 44209**/
            @SerializedName("nap_duration") var dozeDuration: Float = 0f,

            @SerializedName("start_time") var startTime: Long = 0,
            @SerializedName("end_time") var endTime: Long = 0
    )

    data class StepSum(
            var value: Int = 0,
            @SerializedName("start_time") var startTime: Long = 0,
            @SerializedName("end_time") var endTime: Long = 0
    )

    data class DistanceSum(
            var value: Int = 0,
            @SerializedName("start_time") var startTime: Long = 0,
            @SerializedName("end_time") var endTime: Long = 0
    )

    data class CaloriesSum(
            var value: Int = 0,
            @SerializedName("start_time") var startTime: Long = 0,
            @SerializedName("end_time") var endTime: Long = 0
    )
}

