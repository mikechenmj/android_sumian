package com.sumian.sd.buz.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementRecord(
        @SerializedName("achievement")
        val achievement: AchievementX,
        @SerializedName("achievement_id")
        val achievementId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("pop_at")
        val popAt: Any,
        @SerializedName("rewarded_at")
        val rewardedAt: Int
)