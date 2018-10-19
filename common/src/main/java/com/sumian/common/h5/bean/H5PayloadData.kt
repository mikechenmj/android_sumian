package com.sumian.common.h5.bean

import com.sumian.common.utils.JsonUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 17:21
 * desc   :
 * version: 1.0
 */
class H5PayloadData(val page: String, val payload: Map<String, Any>) {
    fun toJson(): String {
        return JsonUtil.toJson(this)
    }
}