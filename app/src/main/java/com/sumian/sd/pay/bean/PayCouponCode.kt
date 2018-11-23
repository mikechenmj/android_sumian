package com.sumian.sd.pay.bean

import com.google.gson.annotations.SerializedName

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
){

    fun tips():String{
        return when{
            expiredAt-updatedAt<=0->{//这里过期
                "此优惠码无效"
            }else->{
                ""
            }
        }


    }
}