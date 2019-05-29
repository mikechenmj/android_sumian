package com.sumian.sd.buz.notification.bean

import com.google.gson.annotations.SerializedName

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/5 14:02
 * desc   :
 * version: 1.0
</pre> *
 */
data class Notification(
        @SerializedName("category")
        var category: Int,
        @SerializedName("created_at")
        var createdAt: Int,
        @SerializedName("data")
        var `data`: NotificationData,
        @SerializedName("data_id")
        var dataId: Int,
        @SerializedName("id")
        var id: String,
        @SerializedName("read_at")
        var readAt: Int,
        @SerializedName("type")
        var type: String
) {
    companion object {
        private val NOTIFICATION_TYPE_PREFIX = "App\\Notifications\\"
        val TYPE_FOLLOW_UP_REFERRAL_NOTICE = NOTIFICATION_TYPE_PREFIX + "FollowUpReferralNotice"//复诊提醒
        val TYPE_FOLLOW_UP_LIFE_NOTICE = NOTIFICATION_TYPE_PREFIX + "FollowUpLifeNotice"//生活提醒
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
