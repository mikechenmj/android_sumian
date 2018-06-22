package com.sumian.sleepdoctor.network.interceptor

import com.sumian.sleepdoctor.network.StatusCode
import okhttp3.Interceptor
import okhttp3.Response

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 16:57
 *     desc   :
 *     299 是服务器定义的 特殊 code，用于业务逻辑异常
 *     299 时，返回的是异常数据，不应该走 200 的解析逻辑，所以要将 code 修改为 499 走异常逻辑
 *     version: 1.0
 * </pre>
 */
class StatusCodeInterceptor : Interceptor {

    companion object {
        fun create(): StatusCodeInterceptor {
            return StatusCodeInterceptor()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val code = response.code()
        if (code == StatusCode.BUSINESS_ERROR_FOR_PRIVATE) {
            return response.newBuilder().code(StatusCode.BUSINESS_ERROR).build()
        }
        return response
    }

}