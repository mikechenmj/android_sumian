package com.sumian.sd.service.coupon.bean

data class Coupon(val id: Int,
                  val Title: String,
                  val time: Int,
                  val status: Int) {


    fun formatTime(): String {
        return "兑换时间：2018.09.14"
    }

    fun formatStatus(): String {
        return "已使用"
    }
}