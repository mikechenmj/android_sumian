package com.sumian.sddoctor.notification.bean

import com.google.gson.annotations.SerializedName

data class NotificationData(
        @SerializedName("plan_start_at") val planStartAt: Int,
        @SerializedName("title") val title: String,
        @SerializedName("content") val content: String,
        @SerializedName("scheme") val scheme: String
)