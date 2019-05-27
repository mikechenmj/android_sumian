package com.sumian.device.data

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/10 10:56
 * desc   :
 * version: 1.0
 */
data class MonitorVersionInfo(
        var channel: MonitorChannel = MonitorChannel.UNKNOWN,
        var softwareVersion: String? = null,
        var hardwareVersion: String? = null,
        var heartBeatLibVersion: String? = null,    //心率库版本号
        var sleepAlgorithmVersion: String? = null    //睡眠算法版本号
)

enum class MonitorChannel {
    UNKNOWN,
    NORMAL,
    CLINIC,
}