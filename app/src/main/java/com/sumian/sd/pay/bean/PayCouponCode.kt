@file:Suppress("DEPRECATION")

package com.sumian.sd.pay.bean

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R
import com.sumian.sd.app.App

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
        return when {
            status != 1 || expiredAt - updatedAt <= 0 -> {//这里过期
                "此优惠码无效"
            }
            status == 1 -> {
                val formatStatus = "已优惠${discount / 100}元"
                val spannableString = SpannableString(formatStatus)
                spannableString.setSpan(ForegroundColorSpan(App.getAppContext().resources.getColor(R.color.b3_color)), 0, formatStatus.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString
            }
            else -> {
                "此优惠码无效"
            }
        }
    }
}