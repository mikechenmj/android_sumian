package com.sumian.sddoctor.log

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 17:33
 * desc   :
 * version: 1.0
 */
class SddLogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() >= 500) {
            val requestInfo = "${request.url()} ${request.body()}"
            val responseInfo = "${response.code()} ${response.message()} ${response.body()}"
            val responseCode = response.code().toString()
            SddLogManager.logHttp(requestInfo, responseInfo, responseCode)
        }
        return response
    }
}