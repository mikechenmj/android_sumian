package com.sumian.sddoctor.service.report.bean

data class DailyMeta(val earliest_day: Int = 0 //用户使用设备后最早一条数据的时间戳,如果最早一条是2018-03-03 则返回2018-03-03 00：00：00时间戳
)
