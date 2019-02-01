package com.sumian.sddoctor.service.report.bean

import java.util.*

/**
 * Created by jzz
 * on 2018/3/5.
 * desc:日报告
 */

class DailyReport : Comparable<DailyReport> {

    var id: Int = 0
    var user_id: Int = 0
    var date: Int = 0//unix时间戳：1,519,833,600，不传默认返回最新
    var sleep_duration: Int = 0//睡眠时长
    var awake_duration: Int = 0//清醒时长
    var deep_duration: Int = 0//深睡时长
    var light_duration: Int = 0//浅睡时长
    var light_duration_percent: Int = 0//浅睡占全天睡眠时长比例*100，如显示64%，则返回64
    var deep_duration_percent: Int = 0//深睡占全天睡眠时长比例*100，如显示64%，则返回64
    var wake_up_mood: Int = 0//苏醒情绪，-1：未填写，0：不太好，1：一般般，2：还可以，3：好极了
    var bedtime_state: ArrayList<String>? = null//睡前状态
    var remark: String? = null//睡眠备注
    var wrote_diary_at: String? = null//填写睡眠日记时间，为 NULL 时表示用户未填写睡眠日记
    var doctors_evaluation: String? = null//医生评价，为空字符串表示医生未评价
    var is_read: Int = 0//医生评价是否已读，0：未读，1：已读；医生填写评价后要把标志改为 0
    var packages: ArrayList<SleepPackage>? = null//睡眠数据包
    var needScrollToBottom = false

    val dateInMillis: Long
        get() = date * 1000L

    override fun compareTo(other: DailyReport): Int {
        return date - other.date
    }

    fun hasReport(): Boolean {
        return id != 0
    }
}
