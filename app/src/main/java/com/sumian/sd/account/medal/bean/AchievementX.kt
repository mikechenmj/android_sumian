package com.sumian.sd.account.medal.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AchievementX(
        @SerializedName("achievement_category_id")
        val achievementCategoryId: Int,
        @SerializedName("context")
        val context: String,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("gain_medal_picture")
        val gainMedalPicture: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("not_gain_medal_picture")
        val notGainMedalPicture: String,
        @SerializedName("record")
        val record: Record?,
        @SerializedName("sentence")
        val sentence: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: Int
) : Parcelable {


    fun isHave(): Boolean = record != null
}