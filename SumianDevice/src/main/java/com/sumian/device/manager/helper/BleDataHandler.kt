package com.sumian.device.manager.helper

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 16:04
 * desc   :
 * version: 1.0
 */
interface BleDataHandler {
    fun handleData(data: ByteArray, cmd: String): Boolean
}