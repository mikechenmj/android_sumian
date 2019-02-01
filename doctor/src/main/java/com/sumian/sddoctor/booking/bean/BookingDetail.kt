package com.sumian.sddoctor.booking.bean

import com.google.gson.annotations.SerializedName

data class BookingDetail(
        @SerializedName("id") val id: Int,
        @SerializedName("booking_date_id") val bookingDateId: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("admin_id") val adminId: Int,
        @SerializedName("type") val type: Int,
        @SerializedName("plan_start_at") val planStartAt: Int,
        @SerializedName("plan_end_at") val planEndAt: Int,
        @SerializedName("started_at") val startedAt: Int,
        @SerializedName("ended_at") val endedAt: Int,
        // 状态 0:待确认，1：已确认 2：进行中 3：通话中 4：已完成 5：已关闭 6：已挂起 7：已取消 8：已结束
        @SerializedName("status") val status: Int,
        @SerializedName("consulting_question") val consultingQuestion: String,
        @SerializedName("add") val add: String,
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("user") val user: User
) {
    fun getPlanStartAtInMillis(): Long {
        return planStartAt * 1000L
    }

    fun getBookingDurationInMillis(): Long {
        return (planEndAt - planStartAt) * 1000L
    }

    fun getBookingDurationInMin(): Int {
        return (planEndAt - planStartAt) / 60
    }
}