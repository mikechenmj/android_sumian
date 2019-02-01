package com.sumian.sddoctor.network.response

import com.google.gson.annotations.SerializedName

data class Links(
        @SerializedName("previous") val previous: Any,
        @SerializedName("next") val next: String
)