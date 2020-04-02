package com.sumian.sd.buz.anxiousandfaith.bean

import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 11:40
 * desc   :
 * version: 1.0
 */
@Parcelize
data class AnxietyData(
        val id: Int = ANXIETY_INVALID_ID,
        @SerializedName("user_id")
        val userId: Int = ANXIETY_INVALID_ID,
        var anxiety: String = "",
        var solution: String = "",
        @SerializedName("remind_at")
        var remindAt: Long = 0,
        var answers: List<AnxietyAnswer> = emptyList(),
        @SerializedName("updated_at")
        var updatedAt: Int = 0,
        @SerializedName("created_at")
        var createdAt: Int = 0
) : Parcelable {

    companion object {
        const val EXTRA_KEY_ANXIETY = "anxiety"
        const val ANSWER_CHECKED_YES = "y"
        const val ANSWER_CHECKED_NO = "n"

        const val ANSWER_HOW_TO_SOLVE_ONE_INDEX = "1"
        const val ANSWER_HOW_TO_SOLVE_TWO_INDEX = "2"
        const val ANSWER_HOW_TO_SOLVE_THREE_INDEX = "3"

        const val ANSWER_INVALID_VALUE = ""

        const val ANSWER_HAS_DETAILED_PLAN_ID = 1
        const val ANSWER_IS_HARD_PROBLEM_ID = 2
        const val ANSWER_HOW_TO_SOLVE_ID = 3

        const val ANXIETY_INVALID_ID = -1
    }

    fun getUpdateAtInMillis(): Long {
        return updatedAt * 1000L
    }

    fun getRemindAtInMillis(): Long {
        if (remindAt == 0L) {
            return createdAt * 1000L
        }
        return remindAt * 1000L
    }

    fun setRemindAtInSecond(millis: Long) {
        remindAt = millis / 1000
    }

    fun getAnswer(id: Int): String {
        var index = id - 1
        if (answers.size > index) {
            return answers[index].answer
        }
        return ANSWER_INVALID_VALUE
    }

    fun hasDetailedPlanChecked(): Boolean {
        return getAnswer(ANSWER_HAS_DETAILED_PLAN_ID) == ANSWER_CHECKED_YES
    }

    fun hardChecked(): Boolean {
        var hardChecked = getAnswer(ANSWER_IS_HARD_PROBLEM_ID) == ANSWER_CHECKED_YES
        return hardChecked

    }

    fun getHowToResolveCheckedId(): String {
        return if (!hardChecked()) ANSWER_INVALID_VALUE else getAnswer(ANSWER_HOW_TO_SOLVE_ID)
    }
}

@Parcelize
data class AnxietyAnswer(val id: Int, val answer: String) : Parcelable