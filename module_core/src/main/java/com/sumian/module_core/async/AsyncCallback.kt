package com.sumian.module_core.async

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 13:56
 * desc   :
 * version: 1.0
 */
interface AsyncCallback<T> {
    fun onSuccess(result: T?) {}
    fun onFailed(code: Int, message: String?) {}
    fun onFinish() {}
}