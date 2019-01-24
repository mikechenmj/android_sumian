package com.sumian.sd.account.achievement.bean

import com.google.gson.annotations.SerializedName

data class LastAchievement(
        @SerializedName("id")
        val id: Int,
        @SerializedName("gain_medal_picture")
        val gainMedalPicture: String,
        @SerializedName("not_gain_medal_picture")
        val notGainMedalPicture: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("sentence")
        val sentence: String,
        @SerializedName("context")
        val context: String,
        @SerializedName("achievement_category_id")
        val achievementCategoryId: Int,
        @SerializedName("type")
        val type: Int,
        @SerializedName("achievement_category")
        val achievement_category: LastRecord
) {

    companion object {
        const val CBTI_TYPE = 0x00
    }
}