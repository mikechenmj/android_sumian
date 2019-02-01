package com.sumian.sd.buz.account.achievement.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:
 */
data class LastAchievementData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("achievement_id")
        val achievementId: Int,
        @SerializedName("rewarded_at")
        val rewardedAt: Int?,
        @SerializedName("pop_at")
        val popAt: Int?,
        @SerializedName("achievement")
        val achievement: LastAchievement,
        val meta: AchievementMeta
) {

    fun isPop(): Boolean = popAt != null
}