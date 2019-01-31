package com.sumian.sd.buz.notification.bean

import com.google.gson.annotations.SerializedName

data class SystemNotificationData(
        @SerializedName("content")
        val content: String, // tuisongceshi new
        @SerializedName("content_detail")
        val contentDetail: String, // content_detailcontent_detail
        @SerializedName("id")
        val id: Int, // 2
        @SerializedName("notice_at")
        val noticeAt: Int // 1544498950
)