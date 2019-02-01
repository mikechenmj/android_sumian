package com.sumian.sddoctor.me.myservice.bean

import com.sumian.sddoctor.util.PriceUtil

data class Packages(
        val id: Int,
        val unit_price: Int,
        val doctor_id: Int,
        val service_package_id: Int,
        val enable: Int,
        val service_package_enable: Int,
        val total_user_count: Int,
        val valid_user_count: Int
) {
    fun isEnable(): Boolean {
        return enable == 1
    }

    fun getPriceString(): String {
        return PriceUtil.formatPrice(unit_price)
    }
}