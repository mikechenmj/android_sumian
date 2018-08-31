package com.sumian.common.network.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody

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
class SpecialRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()

        // doctor/calls 返回的结果是纯字符串，无法进行json解析，这里特殊处理
        if (url != null && url.toString().endsWith("doctor/calls")) {
            val response = chain.proceed(chain.request())
            if (response.code() == 202) {
                val json = "{\"message\":\"{msg}\"}".replace("{msg}", response.body()?.string()
                        ?: "Empty body")
                val responseBody = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), json)
                return response.newBuilder().body(responseBody).build()
            }
        }
        return chain.proceed(request)
    }
}