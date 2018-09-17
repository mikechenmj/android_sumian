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
class ServiceTimeUtil {
    companion object {
        fun formatTimeYYYYMMDD(unixTime: Long): String? {
            return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(unixTime)
        }

        fun formatTimeYYYYMMDDHHMM(unixTime: Long): String? {
            return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(unixTime)
        }
    }
}