package com.sumian.device.callback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 17:37
 * desc   :
 * version: 1.0
 */
interface WriteBleDataCallback {
    fun onSuccess(data: ByteArray)
    fun onFail(code: Int, msg: String)
}