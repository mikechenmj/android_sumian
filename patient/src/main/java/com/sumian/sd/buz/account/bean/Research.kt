package com.sumian.sd.buz.account.bean


import com.google.gson.annotations.SerializedName

data class Research(
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("title")
        val title: String, // 香港CBTI科研
        @SerializedName("type")
        val type: Int // 0:实验组 1：对照组
)