package com.sumian.sd.setting.remind.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reminder(
        @SerializedName("id") val id: Int,
        @SerializedName("reminder_id") val reminderId: Int, // (数据库设计的原因，所以这里用 reminder_id 表示提醒类型，1：睡眠提醒 2：睡眠日记提醒 3:放松训练提醒
        @SerializedName("user_id") val userId: Int,
        @SerializedName("remind_at") val remindAt: String,
        @SerializedName("enable") val enable: Int
) : Parcelable {

    companion object {
        const val SLEEP_DIARY_TYPE = 2
        const val RELAXATION_TRAINING_TYPE = 3
    }

    fun getRemindAtHour(): Int {
        return remindAt.split(":")[0].toInt()
    }

    fun getReminderAtMinute(): Int {
        return remindAt.split(":")[1].toInt()
    }

    fun getReminderHHmm(): String {
        val endIndex = remindAt.lastIndexOf(":")
        return remindAt.substring(-0, endIndex)
    }

    fun isEnable(): Boolean {
        return enable == 1
    }

    fun getRemindAtUnixTime(): Int {
        return TimeUtil.getUnixTimeFromHourAndMinute(getRemindAtHour(), getReminderAtMinute())
    }
}