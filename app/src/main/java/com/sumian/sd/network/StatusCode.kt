package com.sumian.sd.network

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 11:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class StatusCode {
    companion object {
        const val BUSINESS_ERROR_FOR_PRIVATE = 299  // 服务器私有协议，例如阿里OSS status_code = 400 时无法携带 responseBody，服务器兼容处理返回了改值
        const val BUSINESS_ERROR = 499  // 服务器私有协议，业务逻辑错误返回499
    }
}