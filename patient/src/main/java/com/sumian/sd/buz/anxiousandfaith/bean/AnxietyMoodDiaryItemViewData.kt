package com.sumian.sd.buz.anxiousandfaith.bean

import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.event.EmotionData

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/27 10:43
 * desc   :
 * version: 1.0
 */
data class AnxietyMoodDiaryItemViewData(val type: Int = TYPE_ANXIETY, val id: Int, val title: String, val message: String, val time: Long, val emotion: Int = 0) {
    companion object {
        const val TYPE_ANXIETY = 0
        const val TYPE_MOOD_DIARY = 1

        val EMOTION_LIST = listOf(
                EmotionData(0, R.string.emotion_0, R.drawable.mood_diary_icon_facial1_default, R.drawable.mood_diary_icon_facial1_selected),
                EmotionData(1, R.string.emotion_1, R.drawable.mood_diary_icon_facial2_default, R.drawable.mood_diary_icon_facial2_selected),
                EmotionData(2, R.string.emotion_2, R.drawable.mood_diary_icon_facial3_default, R.drawable.mood_diary_icon_facial3_selected),
                EmotionData(3, R.string.emotion_3, R.drawable.mood_diary_icon_facial4_default, R.drawable.mood_diary_icon_facial4_selected),
                EmotionData(4, R.string.emotion_4, R.drawable.mood_diary_icon_facial5_default, R.drawable.mood_diary_icon_facial5_selected),
                EmotionData(5, R.string.emotion_5, R.drawable.mood_diary_icon_facial6_default, R.drawable.mood_diary_icon_facial6_selected)
        )

        fun create(data: AnxietyData): AnxietyMoodDiaryItemViewData {
            return AnxietyMoodDiaryItemViewData(TYPE_ANXIETY, data.id, data.anxiety, data.solution, data.getUpdateAtInMillis())
        }

        fun create(data: MoodDiaryData): AnxietyMoodDiaryItemViewData {
            return AnxietyMoodDiaryItemViewData(TYPE_MOOD_DIARY, data.id, data.scene, data.idea, data.created_at * 1000L, data.emotion_type)
        }

        fun transformAnxietyList(inputList: List<AnxietyData>): ArrayList<AnxietyMoodDiaryItemViewData> {
            val list = ArrayList<AnxietyMoodDiaryItemViewData>()
            for (data in inputList) {
                list.add(create(data))
            }
            return list
        }

        fun transformFaithList(inputList: List<MoodDiaryData>): ArrayList<AnxietyMoodDiaryItemViewData> {
            val list = ArrayList<AnxietyMoodDiaryItemViewData>()
            for (data in inputList) {
                list.add(create(data))
            }
            return list
        }

        fun getEmotionTextRes(emotion : Int): Int {
            return EMOTION_LIST[emotion].textRes
        }

        fun getEmotionImageRes(emotion : Int):Int {
            return EMOTION_LIST[emotion].selectedImageRes
        }
    }
}