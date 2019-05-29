package com.sumian.sd.buz.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class AchievementX(
        @SerializedName("achievement_category")
        val achievementCategory: AchievementCategory,
        @SerializedName("achievement_category_id")
        val achievementCategoryId: Int,
        @SerializedName("context")
        val context: String,
        @SerializedName("gain_medal_picture")
        val gainMedalPicture: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("not_gain_medal_picture")
        val notGainMedalPicture: String,
        @SerializedName("sentence")
        val sentence: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: Int
)