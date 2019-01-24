package com.sumian.sd.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementData(
        @SerializedName("achievements")
        val achievements: List<Achievement>,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: Int,
        val meta: AchievementMeta //当请求为特定类型的才存在 amazing,和请求全部的类目的数据居然不一致? amazing
)