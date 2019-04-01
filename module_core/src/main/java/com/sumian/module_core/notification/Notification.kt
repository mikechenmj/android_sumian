package com.sumian.module_core.notification

import com.google.gson.annotations.SerializedName
import com.sumian.common.utils.TimeUtilV2

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/5 14:02
 * desc   :
 * version: 1.0
</pre> *
 */
data class NotificationCategory(
        @SerializedName("category") val category: Int,  //类别 0:普通消息 2:cbti周报消息
        @SerializedName("notification") val notification: Notification,
        @SerializedName("hasUnread") val hasUnread: Boolean
)

data class Notification(
        @SerializedName("id") val id: String,
        @SerializedName("data_id") var dataId: Int,
        @SerializedName("type") val type: String,
        @SerializedName("data") val data: NotificationData,
        @SerializedName("read_at") var readAt: Int,
        @SerializedName("created_at") val createdAt: Int
) {
    companion object {
        private val NOTIFICATION_TYPE_PREFIX = "App\\Notifications\\"
        val TYPE_FOLLOW_UP_REFERRAL_NOTICE = NOTIFICATION_TYPE_PREFIX + "FollowUpReferralNotice"//复诊提醒
        val TYPE_FOLLOW_UP_LIFE_NOTICE = NOTIFICATION_TYPE_PREFIX + "FollowUpLifeNotice"//生活提醒
    }

    fun getCreatedAtString(): String {
        return TimeUtilV2.formatYYYYMMDDHHMM(createdAt * 1000L)
    }
}

data class NotificationData(
        @SerializedName("content")
        var content: String,
        @SerializedName("scheme")
        var scheme: String,
        @SerializedName("title")
        var title: String
)
