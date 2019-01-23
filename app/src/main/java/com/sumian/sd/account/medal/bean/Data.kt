package com.sumian.sd.account.medal.bean

import com.google.gson.annotations.SerializedName

data class Data(
        @SerializedName("achievements")
        val achievements: List<AchievementX>,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: Int
)