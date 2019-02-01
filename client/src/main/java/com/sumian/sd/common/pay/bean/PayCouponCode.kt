@file:Suppress("DEPRECATION")

package com.sumian.sd.common.pay.bean

import com.google.gson.annotations.SerializedName
import java.util.*

data class PayCouponCode(
        @SerializedName("code")
        val code: String,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("discount")
        val discount: Double,//单位  分
        @SerializedName("expired_at")
        val expiredAt: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("package_id")
        val packageId: Int,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: Int
) {

    fun tips(): CharSequence {
        return when (status) {
            1 -> {
                String.format(Locale.getDefault(), "%s%d%s", "已优惠", (discount / 100).toInt(), "元")
            }
            else -> {
                "此优惠码无效"
            }
        }
    }
}