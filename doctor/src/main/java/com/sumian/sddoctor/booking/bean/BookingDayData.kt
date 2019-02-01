package com.sumian.sddoctor.booking.bean

import com.google.gson.annotations.SerializedName

data class BookingDayData(
        @SerializedName("date") val date: Int,
        @SerializedName("bookings") val bookings: List<Booking>
) : Comparable<BookingDayData> {

    override fun compareTo(other: BookingDayData): Int {
        return date - other.date
    }

    fun getDateInMillis(): Long {
        return date * 1000L
    }
}