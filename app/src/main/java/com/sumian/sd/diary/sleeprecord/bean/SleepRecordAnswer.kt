package com.sumian.sd.diary.sleeprecord.bean

import com.sumian.common.utils.TimeUtilV2
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/1 9:47
 * desc   :
 * version: 1.0
</pre> *
 */
class SleepRecordAnswer {
    /**
     * bed_at : 23:00
     * sleep_at : 23:00
     * wake_up_at : 07:00
     * get_up_at : 07:00
     * wake_times : 2
     * wake_minutes : 30
     * energetic : 4
     * sleepless_factor : ["饮酒","喝茶/咖啡","身体不适","吃太饱","有心事","睡前运动过量"]
     * other_sleep_times : 1
     * other_sleep_total_minutes : 15
     * sleep_pills : [{"name":"唑吡坦","amount":"1片","time":"早饭前／后"},{"name":"唑吡坦","amount":"1片","time":"午饭前／后"},{"name":"咪达唑仑","amount":"1.75片","time":"午饭前／后"},{"name":"硝西泮","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"},{"name":"艾司唑仑","amount":"2.75片","time":"午饭前／后"}]
     * remark : 我昨晚睡得很好^_^
     */

    var bed_at: String = ""
    var sleep_at: String = ""
    var wake_up_at: String = ""
    var get_up_at: String = ""
    var wake_times: Int = 0
    var wake_minutes: Int = 0
    var energetic: Int = 0
    var other_sleep_times: Int = 0
    var other_sleep_total_minutes: Int = 0
    var remark: String? = null
    var sleep_pills: List<SleepPill>? = null

    private fun parseTimeStr(s: String): Long {
        return TimeUtilV2.parseTimeStr(s)
    }

    fun getBedAtInMillis(): Long {
        return parseTimeStr(bed_at)
    }

    fun getSleepAtInMillis(): Long {
        return parseTimeStr(sleep_at)
    }

    fun getWakeUpAtInMillis(): Long {
        return parseTimeStr(wake_up_at)
    }

    fun getGetUpAtInMillis(): Long {
        return parseTimeStr(get_up_at)
    }

    fun getWakeDurationInMillis(): Int {
        return wake_minutes * 60 * 1000

    }
}
