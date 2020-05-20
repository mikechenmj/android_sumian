package com.sumian.device.net

import com.sumian.device.authentication.AuthenticationManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:token 认证鉴权拦截器
 *
 * 1.为每个需要鉴权的 api 请求加入鉴权机制
 * 2.根据后台 token 过期失效/安全机制，主动刷新token
 */

class TokenInterceptor : Interceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER = "Bearer "
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
                .request()
                .newBuilder()

        val request = builder.addHeader(
                AUTHORIZATION_HEADER,
                "$BEARER${AuthenticationManager.mToken}")
                .addHeader("X-Api-Ver", "1.0")
                .build()

        val response = chain.proceed(request)
        val authorizationHeader = response.header(AUTHORIZATION_HEADER)
        authorizationHeader?.let {
            AuthenticationManager.mToken = it.removePrefix(BEARER)
        }
        return response
    }

}
