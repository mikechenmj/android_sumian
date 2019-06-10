package com.sumian.device.data

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/10 11:09
 * desc   :
 * version: 1.0
 */
data class SleepMasterVersionInfo(
        var softwareVersion: String? = null,
        var hardwareVersion: String? = null,
        var headDetectAlgorithmVersion: String? = null,
        var protocolVersion: Int // 协议版本号
)