package com.sumian.sd.account.medal.bean

import com.google.gson.annotations.SerializedName

data class Achievement(
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("meta")
        val meta: Meta
)