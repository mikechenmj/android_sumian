package com.sumian.sd.buz.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementCategory(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("type")
        val type: Int
)