package com.sumian.sd.account.medal.bean

import com.google.gson.annotations.SerializedName

data class Meta(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("pagination")
        val pagination: Pagination,
        @SerializedName("qr_code")
        val qrCode: String
)