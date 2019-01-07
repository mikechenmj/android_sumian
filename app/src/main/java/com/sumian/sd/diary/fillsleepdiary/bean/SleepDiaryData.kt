package com.sumian.sd.diary.fillsleepdiary.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/18 14:22
 * desc   :
 * version: 1.0
 */
data class SleepDiaryData(
        var date: Int,
        var try_to_sleep_at: String,
        var sleep_at: String,
        var wake_up_at: String,
        var get_up_at: String,
        var wake_times: Int,
        var wake_minutes: Int,
        var other_sleep_times: Int,
        var other_sleep_total_minutes: Int,
        var energetic: Int,
        var sleep_pills: String?,
        var remark: String?
)