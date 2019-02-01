package com.sumian.sddoctor.notification.bean

import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.network.response.Pagination

data class Meta(
        @SerializedName("pagination") val pagination: Pagination,
        @SerializedName("unread_num") val unreadNum: Int
)