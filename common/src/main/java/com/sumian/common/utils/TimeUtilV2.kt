package com.sumian.common.utils

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
    }
}