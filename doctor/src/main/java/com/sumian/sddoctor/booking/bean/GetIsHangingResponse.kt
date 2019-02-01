package com.sumian.sddoctor.booking.bean

import com.google.gson.annotations.SerializedName

data class GetIsHangingResponse(
        @SerializedName("hanging") val hanging: Boolean
)