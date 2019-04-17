package com.sumian.sddoctor.login.login.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialiteInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("type") val type: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("open_id") val openId: String,
        @SerializedName("union_id") val unionId: String,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("app") val app: Int,
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("updated_at") val updatedAt: Int
) : Parcelable