package com.sumian.sd.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementResponse(
        @SerializedName("data")
        val `data`: List<AchievementData>,
        @SerializedName("meta")
        val achievementMeta: AchievementMeta
)