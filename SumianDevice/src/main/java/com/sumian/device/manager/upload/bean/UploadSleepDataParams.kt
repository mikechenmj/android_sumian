package com.sumian.device.manager.upload.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 14:16
 * desc   :
 * version: 1.0
 */
data class UploadSleepDataParams(
        val fileName: String, //数据文件名,包含文件名后缀
        val sn: String?,        //监测仪 SN
        val sleeper_sn: String?,//速眠仪 SN
        val type: Int,         //数据类型，1：睡眠特征值，2：事件日志, 默认值: 1
        val app_receive_started_at: Int, // APP收取数据开始时间
        val app_receive_ended_at: Int // APP收取数据结束时间
)