package com.sumian.sddoctor.me.mywallet.bean

import android.os.Parcelable
import com.sumian.sddoctor.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WithdrawRecord(
        val id: Int,
        val doctor_id: Int,
        val amount: Long,
        val tax: Int,
        val sn: String,
        val status: Int, // 0：审核中，1：审核通过，2：审核不通过
        val auditor_id: Int,
        val audited_at: Int,
        val remark: String?,
        val created_at: Int,
        val updated_at: Int
) : Parcelable {

    fun getCreateAtInMillis(): Long {
        return created_at * 1000L
    }

    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }

    fun getStatusTextRes(): Int {
        return when (status) {
            0 -> R.string.approve_ing
            1 -> R.string.approve_passed
            2 -> R.string.approve_not_passed
            else -> R.string.approve_ing
        }
    }

    fun getStatusTextColorRes(): Int {
        return when (status) {
            0 -> R.color.b3_color
            1 -> R.color.t3_color
            2 -> R.color.t4_color
            else -> R.color.b3_color
        }
    }

    fun getStatusHintTextRes(): Int {
        return when (status) {
            0, 1 -> R.string.please_wait_one_to_three_day_blala
            2 -> R.string.withdraw_send_back
            else -> R.string.please_wait_one_to_three_day_blala
        }
    }
}