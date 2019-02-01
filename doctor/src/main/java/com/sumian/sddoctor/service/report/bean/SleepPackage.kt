package com.sumian.sddoctor.service.report.bean

/**
 * Created by sm
 * on 2018/3/6.
 * desc:睡眠数据包
 */

data class SleepPackage(val id: Int = 0,//包id
                        val sleep_id: Int = 0,//睡眠 id
                        val index: Int = 0,//包索引
                        val from_time: Int = 0,//此条数据表示的起始时间
                        val to_time: Int = 0,//此条数据表示的结束时间
                        val state: Int = 0,//状态 0：清醒，1：REM，2：浅睡，3：深睡
                        val count: Int = 0,//包数量
                        var duration: Int = 0//一次睡眠状态持续时间
) {


    fun calculateDuration() {
        duration = to_time - from_time
    }

}
