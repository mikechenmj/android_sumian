package com.sumian.sddoctor.booking.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Booking(
        @SerializedName("id") var id: Int,
        @SerializedName("booking_date_id") var bookingDateId: Int,
        @SerializedName("user_id") var userId: Int,
        @SerializedName("type") var type: Int,
        @SerializedName("plan_start_at") var planStartAt: Int,
        @SerializedName("plan_end_at") var planEndAt: Int,
        @SerializedName("started_at") var startedAt: Int,
        @SerializedName("ended_at") var endedAt: Int,
        @SerializedName("status") var status: Int,//状态 0:待确认，1：已确认 2：进行中 3：通话中 4：已完成 5：已关闭 6：已挂起 7：已取消 8：已结束
        @SerializedName("created_at") var createdAt: Int,
        @SerializedName("user") var user: User
) : Parcelable {
    companion object {
        const val STATUS_WAIT_CONFIRM = 0
        const val STATUS_ALREADY_CONFIRM = 1
        const val STATUS_GOING = 2
        const val STATUS_CALLING = 3
        const val STATUS_COMPLETE = 4
        const val STATUS_CLOSE = 5
        const val STATUS_HANG = 6
        const val STATUS_CANCELED = 7
        const val STATUS_FINISH = 8
    }

    fun getStartedAtInMillis(): Long {
        return startedAt * 1000L
    }

    fun getEndedAtInMillis(): Long {
        return endedAt * 1000L
    }

    fun getPlanStartAtInMillis(): Long {
        return planStartAt * 1000L
    }

    fun getPlanEndAtInMillis(): Long {
        return planEndAt * 1000L
    }
}