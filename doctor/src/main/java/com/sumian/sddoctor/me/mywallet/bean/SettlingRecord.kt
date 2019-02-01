package com.sumian.sddoctor.me.mywallet.bean

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.R

data class SettlingRecord(
        @SerializedName("amount")
        val amount: Long, // 100000
        @SerializedName("content")
        val content: String, // CBTI
        @SerializedName("created_at")
        val createdAt: Int, // 1548403562
        @SerializedName("credited_at")
        val creditedAt: Int, // 1549008362
        @SerializedName("doctor_id")
        val doctorId: Int, // 10
        @SerializedName("explanation")
        val explanation: String,
        @SerializedName("id")
        val id: Int, // 33
        @SerializedName("sn")
        val sn: String, // 1901255484035621353
        @SerializedName("status")
        val status: Int, // 0: 结算中，1: 待核实
        @SerializedName("type")
        val type: Int, // 0
        @SerializedName("updated_at")
        val updatedAt: Int // 1548403562
) {

    companion object {
        // 0: 结算中，1: 待核实
        const val STATUS_SETTLING = 0
        const val STATUS_CHECKING = 1
    }

    fun getStatusText(context: Context): String {
        val stringArray = context.resources.getStringArray(R.array.settling_status_array)
        return if (status < stringArray.size) {
            stringArray[status]
        } else {
            context.resources.getString(R.string.others)
        }
    }

    fun getStatusTextColorRes(): Int {
        return if (status == 0) {
            R.color.b3_color
        } else {
            R.color.t4_color
        }
    }

    fun getTypeString(context: Context): String {
        return if (type == 0) {
            context.resources.getString(R.string.income)
        } else {
            context.resources.getString(R.string.others)
        }
    }
}