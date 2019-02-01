package com.sumian.sddoctor.booking.bean

import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.network.response.Meta

data class GetBookingsResponse(
        @SerializedName("data") val data: MutableList<BookingDayData>,
        @SerializedName("meta") val meta: Meta
)