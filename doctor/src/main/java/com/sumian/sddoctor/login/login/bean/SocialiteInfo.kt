package com.sumian.sddoctor.login.login.bean

import com.google.gson.annotations.SerializedName

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
)