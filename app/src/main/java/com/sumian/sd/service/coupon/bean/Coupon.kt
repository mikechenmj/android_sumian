package com.sumian.sd.service.coupon.bean

import com.sumian.sd.R
import com.sumian.sd.app.App
import java.text.SimpleDateFormat
import java.util.*

data class Coupon(val id: Int,
                  val redeemed_at: Int,//兑换时间
                  val batch: Batch,//批次信息
                  val status: Int) {//使用状态，0：未使用，1：已使用


    companion object {

        private const val UNUSED_STATUS = 0
        private const val USED_STATUS = 1
    }


    fun formatTime(): String {
        val sdfTime = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(redeemed_at * 1000L))
        return "兑换时间：$sdfTime"
    }

    fun formatStatus(): String {
        return when (status) {
            USED_STATUS -> {
                App.getAppContext().resources.getString(R.string.used)
            }
            UNUSED_STATUS -> {
                App.getAppContext().resources.getString(R.string.unused)
            }
            else -> {
                App.getAppContext().resources.getString(R.string.unused)
            }
        }
    }


    data class Batch(val name: String)//兑换码名字
}