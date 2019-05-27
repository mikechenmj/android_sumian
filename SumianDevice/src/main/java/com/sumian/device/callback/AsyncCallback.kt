package com.sumian.device.callback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 16:28
 * desc   :
 * version: 1.0
 */
interface AsyncCallback<T> {
    fun onSuccess(data: T? = null)
    fun onFail(code: Int, msg: String)
}