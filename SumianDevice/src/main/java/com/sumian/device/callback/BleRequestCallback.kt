package com.sumian.device.callback

import com.clj.fastble.exception.BleException

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/15 15:44
 * desc   :
 * version: 1.0
 */

interface BleRequestCallback {
    companion object {
        const val ERROR_CODE_TIMEOUT = BleException.ERROR_CODE_TIMEOUT
        const val ERROR_CODE_GATT = BleException.ERROR_CODE_GATT
        const val ERROR_CODE_OTHER = BleException.ERROR_CODE_OTHER
    }

    fun onResponse(data: ByteArray, hexString: String)
    fun onFail(code: Int, msg: String)
}