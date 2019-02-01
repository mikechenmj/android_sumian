package com.sumian.sd.common.network.interceptor

import com.sumian.sd.common.log.SdLogManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 17:33
 * desc   :
 * version: 1.0
 */
class SdLogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() >= 500) {
            val requestInfo = "${request.url()} ${request.body()}"
            val responseInfo = "${response.code()} ${response.message()} ${response.body()}"
            SdLogManager.logHttp(requestInfo, responseInfo)
        }
        return response
    }
}