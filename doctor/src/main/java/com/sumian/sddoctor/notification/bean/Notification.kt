package com.sumian.sddoctor.notification.bean

import com.google.gson.annotations.SerializedName

data class Notification(
        @SerializedName("id") val id: String,
        @SerializedName("data_id") var dataId: Int,
        @SerializedName("type") val type: String,
        @SerializedName("data") val data: NotificationData,
        @SerializedName("read_at") var readAt: Int,
        @SerializedName("created_at") val createdAt: Int
) {
    fun getReadAtInMillis(): Long {
        return readAt * 1000L
    }

    fun getCreateAtInMillis(): Long {
        return createdAt * 1000L
    }
}