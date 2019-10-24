package com.sumian.sd.buz.anxiousandfaith.bean

import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.constant.MoodDiaryType
import com.sumian.sd.buz.anxiousandfaith.event.EmotionData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoodDiaryData(
        val id: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("emotion_type")
        var emotionType: Int,
        var emotions: List<String>,
        var scene: String,
        @SerializedName("irrational_belief")
        var irrationalBelief: String,
        @SerializedName("cognition_bias")
        var cognitionBias: List<String>,
        @SerializedName("irrational_belief_result")
        var irrationalBeliefResult: String,
        var idea: String,
        @SerializedName("rational_belief")
        var rationalBelief: String,
        @SerializedName("rational_belief_result")
        var rationalBeliefResult: String,
        @SerializedName("created_at")
        var createdAt: Int
) : Parcelable {

    companion object {
        const val EXTRA_KEY_MOOD_DIARY = "mood_diary"
        const val EXTRA_MOOD_DIARY_TYPE: String = "extra_mood_diary_type"
        const val EXTRA_MOOD_DIARY_LABEL: String = "extra_mood_diary_label"

        val EMOTION_LIST = listOf(
                EmotionData(MoodDiaryType.SAD.value, R.string.emotion_0, R.drawable.mood_diary_icon_facial1_default, R.drawable.mood_diary_icon_facial1_selected),
                EmotionData(MoodDiaryType.ANXIOUS.value, R.string.emotion_1, R.drawable.mood_diary_icon_facial2_default, R.drawable.mood_diary_icon_facial2_selected),
                EmotionData(MoodDiaryType.MAD.value, R.string.emotion_2, R.drawable.mood_diary_icon_facial3_default, R.drawable.mood_diary_icon_facial3_selected),
                EmotionData(MoodDiaryType.CALM.value, R.string.emotion_3, R.drawable.mood_diary_icon_facial4_default, R.drawable.mood_diary_icon_facial4_selected),
                EmotionData(MoodDiaryType.EASY.value, R.string.emotion_4, R.drawable.mood_diary_icon_facial5_default, R.drawable.mood_diary_icon_facial5_selected),
                EmotionData(MoodDiaryType.PLEASURE.value, R.string.emotion_5, R.drawable.mood_diary_icon_facial6_default, R.drawable.mood_diary_icon_facial6_selected),
                EmotionData(MoodDiaryType.ANGRY.value, R.string.mood_diary_angry_text, R.drawable.mood_diary_icon_facial3_default, R.drawable.mood_diary_icon_facial3_selected),
                EmotionData(MoodDiaryType.UNHAPPY.value, R.string.mood_diary_unhappy_text, R.drawable.mood_diary_icon_facial1_default, R.drawable.mood_diary_icon_facial1_selected),
                EmotionData(MoodDiaryType.DULL.value, R.string.mood_diary_dull_text, R.drawable.mood_diary_icon_facial4_default, R.drawable.mood_diary_icon_facial4_selected),
                EmotionData(MoodDiaryType.HAPPEN.value, R.string.mood_diary_happen_text, R.drawable.mood_diary_icon_facial5_default, R.drawable.mood_diary_icon_facial5_selected),
                EmotionData(MoodDiaryType.EXCITED.value, R.string.mood_diary_excited_text, R.drawable.mood_diary_icon_facial6_default, R.drawable.mood_diary_icon_facial6_selected)
        )

        fun getEmotionTextRes(emotionType: Int): Int {
            return EMOTION_LIST[emotionType].textRes
        }

        fun getEmotionImageRes(emotionType: Int): Int {
            return EMOTION_LIST[emotionType].selectedImageRes
        }

        fun isPositiveMoodType(moodDiaryType: Int): Boolean {
            return moodDiaryType == MoodDiaryType.DULL.value || moodDiaryType == MoodDiaryType.HAPPEN.value || moodDiaryType == MoodDiaryType.EXCITED.value
        }
    }

    fun getEmotionTextRes(): Int {
        return getEmotionTextRes(emotionType)
    }

    fun getEmotionImageRes(): Int {
        return getEmotionImageRes(emotionType)
    }

    fun getUpdateAtInMillis(): Long {
        return createdAt * 1000L
    }

    fun isPositiveMoodType(): Boolean {
        return isPositiveMoodType(emotionType)
    }

    fun isFillChallenge(): Boolean {
        return !TextUtils.isEmpty(irrationalBelief) && !TextUtils.isEmpty(irrationalBeliefResult) && cognitionBias.isNotEmpty()
    }

    fun isFillReasonableBelief(): Boolean {
        return !TextUtils.isEmpty(idea) && !TextUtils.isEmpty(rationalBelief) && !TextUtils.isEmpty(rationalBeliefResult)
    }

    fun isFillAll(): Boolean {
        return isFillReasonableBelief() && isFillChallenge()
    }

    interface MoodDiaryDataOwner {
        fun getMoodDiaryData(): MoodDiaryData?
        fun setMoodDiaryData(moodDiaryData: MoodDiaryData?)
    }
}