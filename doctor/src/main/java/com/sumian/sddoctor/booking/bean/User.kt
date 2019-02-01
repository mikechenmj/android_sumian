package com.sumian.sddoctor.booking.bean

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        @SerializedName("id") var id: Int,
        @SerializedName("mobile") var mobile: String,
        @SerializedName("nickname") var nickname: String,
        @SerializedName("name") var name: String,
        @SerializedName("gender") var gender: String,
        @SerializedName("age") var age: String,
        @SerializedName("real_name") var realName: String
) : Parcelable {
    fun getNameOrNickname(): String {
        return if (TextUtils.isEmpty(realName)) {
            if (TextUtils.isEmpty(name)) {
                if (TextUtils.isEmpty(nickname)) {
                    ""
                } else {
                    nickname
                }
            } else {
                name
            }
        } else {
            realName
        }
    }
}