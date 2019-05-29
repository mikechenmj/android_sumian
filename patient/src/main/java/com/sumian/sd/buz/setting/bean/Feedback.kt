package com.sumian.sd.buz.setting.bean

import com.google.gson.annotations.SerializedName

data class Feedback(val id: Int,
                    @SerializedName("upload_at") val uploadAt: Int,
                    val content: String)