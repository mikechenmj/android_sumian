package com.sumian.sd.buz.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementMeta(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("pagination")
        val pagination: Pagination,
        @SerializedName("qr_code")
        val qrCode: String
)