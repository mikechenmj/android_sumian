package com.sumian.common.network.error

/**
 * <pre>
 * @author : Zhan Xuzhao
 * time   : 2018/8/6 13:56
 * desc   :
 * version: 1.0
</pre> *
 */
object ErrorCode {

    const val STATUS_CODE_ERROR_UNKNOWN = 0
    const val UNKNOWN = -1
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val SERVICE_UNAVAILABLE = 503

    const val BUSINESS_ERROR_FOR_ALIBABA_OSS = 299  // 服务器私有协议，阿里OSS status_code = 400 时无法携带 responseBody，服务器兼容处理返回了该值
    const val BUSINESS_ERROR = 499  // 服务器私有协议，业务逻辑错误返回499,

}
