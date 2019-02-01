package com.sumian.sddoctor.network.response

import com.google.gson.annotations.SerializedName

data class Meta(
        @SerializedName("pagination") val pagination: Pagination
)