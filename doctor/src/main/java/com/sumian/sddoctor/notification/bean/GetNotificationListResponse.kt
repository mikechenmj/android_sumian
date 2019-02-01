package com.sumian.sddoctor.notification.bean

import com.google.gson.annotations.SerializedName

data class GetNotificationListResponse(
        @SerializedName("data") val data: List<Notification>,
        @SerializedName("meta") val meta: Meta
)