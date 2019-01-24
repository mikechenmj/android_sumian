package com.sumian.sd.account.achievement.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Achievement(
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

    companion object {
        const val CBTI_TYPE = 0x00
    }


    fun isHave(): Boolean = record != null
}