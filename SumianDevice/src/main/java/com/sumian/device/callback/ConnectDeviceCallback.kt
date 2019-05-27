package com.sumian.device.callback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 15:46
 * desc   :
 * version: 1.0
 */
interface ConnectDeviceCallback {
    fun onStart()
    fun onSuccess()
    fun onFail(code: Int, msg: String)
}