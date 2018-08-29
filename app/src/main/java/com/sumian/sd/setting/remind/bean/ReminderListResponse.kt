package com.sumian.sd.setting.remind.bean

import com.google.gson.annotations.SerializedName

data class ReminderListResponse(
        @SerializedName("data") val data: List<Reminder>,
        @SerializedName("meta") val meta: Meta
)