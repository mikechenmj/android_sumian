package com.sumian.sd.buz.anxiousandfaith.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoodDiaryData(
        val scene: String,
        val idea: String,
        val emotion_type: Int,
        val user_id: Int,
        val updated_at: Int,
        val created_at: Int,
        val id: Int
) : Parcelable {

    companion object {
        const val EXTRA_KEY_MOOD_DIARY = "mood_diary"
    }
    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }
}