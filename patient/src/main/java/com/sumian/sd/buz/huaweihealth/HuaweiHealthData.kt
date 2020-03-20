package com.sumian.sd.buz.huaweihealth

import com.google.gson.annotations.SerializedName

data class HuaweiHealthData(var gender: String = "",
                            var birthday: String = "",
                            var height: String = "",
                            var weight: String = "",
                            @SerializedName("core_sleeps")
                            var coreSleeps: ArrayList<CoreSleep> = ArrayList(),
                            @SerializedName("step_sum")
                            var stepSum: ArrayList<StepSum> = ArrayList(),
                            @SerializedName("distance_sum")
                            var distanceSum: ArrayList<DistanceSum> = ArrayList(),
                            @SerializedName("calories_sum")
                            var caloriesSum: ArrayList<CaloriesSum> = ArrayList()) {

    data class CoreSleep(
            /**44209**/
            @SerializedName("night_sleep_duration") var nightSleepDuration: String = "",
            /**44101**/
            @SerializedName("rem_sleep_duration") var remSleepDuration: String = "",
            /**44102**/
            @SerializedName("deep_sleep_duration") var deepSleepDuration: String = "",
            /**44103**/
            @SerializedName("light_sleep_duration") var lightSleepDuration: String = "",
            /**44105**/
            @SerializedName("total_sleep_duration") var totalSleepDuration: String = "",
            /**44201**/
            @SerializedName("fall_asleep_time") var fallAsleepTime: String = "",
            /**44202**/
            @SerializedName("wake_up_time") var wakeUpTime: String = "",
            /**44106**/
            @SerializedName("deep_sleep_continuity_score") var deepSleepContinuityScore: String = "",
            /**44107**/
            @SerializedName("number_of_sleep_wakefulness") var numberOfSleepWakefulness: String = "",
            /**44203**/
            @SerializedName("sleep_score") var SleepScore: String = "",
            /**44105 - 44209**/
            @SerializedName("doze_duration") var dozeDuration: String = "",

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

