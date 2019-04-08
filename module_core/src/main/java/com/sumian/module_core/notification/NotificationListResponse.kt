package com.sumian.module_core.notification

import com.google.gson.annotations.SerializedName
import com.sumian.common.network.response.Pagination

data class NotificationListResponse(
        @SerializedName("data")
        val `data`: List<Notification>,
        @SerializedName("meta")
        val meta: Meta
) {
    data class Meta(
            @SerializedName("pagination")
            val pagination: Pagination,
            @SerializedName("unread_num")
            val unreadNum: Int // 2
    )
}