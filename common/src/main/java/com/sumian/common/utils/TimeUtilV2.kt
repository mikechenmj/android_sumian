package com.sumian.common.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 16:28
 * desc   :
 * version: 1.0
 */
class TimeUtilV2 {
    companion object {
        fun formatDate(pattern: String, timeInMillis: Long): String {
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            return format.format(Date(timeInMillis))
        }

        fun formatTimeYYYYMMDD(time: Long): String {
            return formatDate("yyyy.MM.dd", time)
        }

        fun formatTimeYYYYMMDD_HHMM(time: Long): String {
            return formatDate("yyyy.MM.dd HH:mm", time)
        }

        fun getCalendar(time: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.time = Date(time)
            return calendar
        }

        /**
         * @param time 时间戳
         * @return 入参时间当天00:00的时间戳
         */
        fun getStartTimeOfTheDay(time: Long): Long {
            val calendar = getCalendar(time)
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE), 0, 0, 0)
            return calendar.timeInMillis
        }

        /**
         * return t1 距离 t2 的天数
         */
        fun getDayDistance(t1: Long, t2: Long): Long {
            return (getStartTimeOfTheDay(t1) - getStartTimeOfTheDay(t2)) / DateUtils.DAY_IN_MILLIS
        }
    }
}