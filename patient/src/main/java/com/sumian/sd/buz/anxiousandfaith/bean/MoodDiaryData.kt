package com.sumian.sd.buz.anxiousandfaith.bean

import android.os.Parcelable
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.event.EmotionData
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
        val EMOTION_LIST = listOf(
                EmotionData(MoodDiaryType.SAD.ordinal, R.string.mood_diary_sad_text, R.drawable.mood_diary_icon_facial1_default, R.drawable.mood_diary_icon_facial1_selected),
                EmotionData(MoodDiaryType.ANXIOUS.ordinal, R.string.emotion_1, R.drawable.mood_diary_icon_facial2_default, R.drawable.mood_diary_icon_facial2_selected),
                EmotionData(MoodDiaryType.ANGRY.ordinal, R.string.mood_diary_angry_text, R.drawable.mood_diary_icon_facial3_default, R.drawable.mood_diary_icon_facial3_selected),
                EmotionData(MoodDiaryType.DULL.ordinal, R.string.mood_diary_dull_text, R.drawable.mood_diary_icon_facial4_default, R.drawable.mood_diary_icon_facial4_selected),
                EmotionData(MoodDiaryType.HAPPEN.ordinal, R.string.mood_diary_happen_text, R.drawable.mood_diary_icon_facial5_default, R.drawable.mood_diary_icon_facial5_selected),
                EmotionData(MoodDiaryType.EXCITED.ordinal, R.string.mood_diary_excited_text, R.drawable.mood_diary_icon_facial6_default, R.drawable.mood_diary_icon_facial6_selected)
        )
    }

    fun getEmotionTextRes(): Int {
        return EMOTION_LIST[emotion_type].textRes
    }

    fun getEmotionImageRes(): Int {
        return EMOTION_LIST[emotion_type].selectedImageRes
    }

    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }
}