package com.sumian.sd.buz.anxiousandfaith.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FaithData(
        val scene: String,
        val idea: String,
        val emotion_type: Int,
        val user_id: Int,
        val updated_at: Int,
        val created_at: Int,
        val id: Int
) : Parcelable {
    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }
}