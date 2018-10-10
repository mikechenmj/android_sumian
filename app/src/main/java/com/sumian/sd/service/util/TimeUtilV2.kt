package com.sumian.sd.service.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 16:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class TimeUtilV2 {
    companion object {
        fun formatYYYYMMDD(time: Long): String {
            return SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(time)
        }

        fun formatYYYYMMDDHHMM(time: Long): String {
            return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(time)
        }

        fun formatYYYYMMDD(unixTime: Int): String {
            return formatYYYYMMDD(unixTime * 1000L)
        }

        fun formatYYYYMMDDHHMM(unixTime: Int): String {
            return formatYYYYMMDDHHMM(unixTime * 1000L)
        }
    }
}