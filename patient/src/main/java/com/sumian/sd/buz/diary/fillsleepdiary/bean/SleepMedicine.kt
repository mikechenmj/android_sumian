package com.sumian.sd.buz.diary.fillsleepdiary.bean

import com.google.gson.annotations.SerializedName

data class SleepMedicine(
        @SerializedName("description")
        val description: String, // 山东省高分数搜索
        @SerializedName("enable")
        val enable: Int, // 1
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String, // 唑吡坦（思诺思）
        @SerializedName("weight")
        val weight: Int // 88
)