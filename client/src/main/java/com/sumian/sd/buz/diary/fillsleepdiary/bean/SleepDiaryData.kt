package com.sumian.sd.buz.diary.fillsleepdiary.bean

import android.text.format.DateUtils
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepPill
import java.util.*


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/18 14:22
 * desc   :
 * version: 1.0
 */
data class SleepDiaryData(
        var date: Int,
        var try_to_sleep_at: Int?,
        var sleep_at: Int?,
        var wake_up_at: Int?,
        var get_up_at: Int?,
        var wake_times: Int,
        var wake_minutes: Int,
        var other_sleep_times: Int,
        var other_sleep_total_minutes: Int,
        var energetic: Int,
        var sleep_pills: List<SleepPill>?,
        var remark: String?,
        var timeZone: Int = (TimeZone.getDefault().rawOffset / DateUtils.HOUR_IN_MILLIS).toInt()
)