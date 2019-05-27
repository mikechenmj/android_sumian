package com.sumian.device.callback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/10 15:34
 * desc   : Ble 通选读写回调
 * version: 1.0
 */
interface BleCommunicationWatcher {
    fun onRead(data: ByteArray, hexString: String) {}
    fun onWrite(data: ByteArray, hexString: String, success: Boolean, errorMsg: String?) {}
}