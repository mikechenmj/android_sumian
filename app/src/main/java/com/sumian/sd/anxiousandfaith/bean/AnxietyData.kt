package com.sumian.sd.anxiousandfaith.bean

import android.os.Parcelable
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
        val anxiety: String,
        val solution: String,
        val user_id: Int,
        val updated_at: Int,
        val created_at: Int,
        val id: Int
) : Parcelable {
    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }
}