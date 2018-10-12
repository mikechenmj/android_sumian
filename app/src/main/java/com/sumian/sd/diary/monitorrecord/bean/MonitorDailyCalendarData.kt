package com.sumian.sd.diary.monitorrecord.bean

data class MonitorDailyCalendarData(
        val id: Int,
        val date: Int,
        val is_read: Boolean,
        val is_today: Boolean,
        val has_doctors_evaluation: Boolean
)