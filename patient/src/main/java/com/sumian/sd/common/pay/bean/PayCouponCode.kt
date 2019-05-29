@file:Suppress("DEPRECATION")

package com.sumian.sd.common.pay.bean

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R

data class PayCouponCode(
        @SerializedName("code")
        val code: String,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("discount")
        val discount: Long,//单位  分
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

    fun isValid(): Boolean {
        return status == 1
    }

    fun getTips(context: Context, maxDiscount: Long): String {
        return if (isValid()) {
            if (maxDiscount - discount > 0) {
                "已优惠${String.format("%.2f", discount * 1.0 / 100)}元"
            } else {
                context.getString(R.string.discount_is_too_much)
            }
        } else {
            "此优惠码无效"
        }
    }

}