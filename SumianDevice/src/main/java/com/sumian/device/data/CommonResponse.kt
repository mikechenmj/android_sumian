package com.sumian.device.data

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/10 12:04
 * desc   :
 * version: 1.0
 */
data class CommonResponse<T>(var success: Boolean, var msg: String? = null, var data: T? = null)