package com.sumian.sd.service.tel.bean

import java.util.*

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
class BookingTime {

    var unixTime = 0


    fun formatDate(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime * 1000L
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        calendar.set(year, month, date, 0, 0, 0)
        val tmpBeginTimeInMillis = calendar.timeInMillis
        return if (tmpBeginTimeInMillis == isToday()) {
            "今天"
        } else {
            "${month + 1}-$date"
        }
    }

    private fun isToday(): Long {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        calendar.set(year, month, date, 0, 0, 0)
        return calendar.timeInMillis
    }
}