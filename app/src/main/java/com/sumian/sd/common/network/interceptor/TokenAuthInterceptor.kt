package com.sumian.sd.common.network.interceptor

import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.Token
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

class TokenAuthInterceptor private constructor() : Interceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHORIZATION_HEADER_BEARER_PART = "Bearer "

        @JvmStatic
        fun create(): Interceptor {
            return TokenAuthInterceptor()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
                .request()
                .newBuilder()

        AppManager.getAccountViewModel().tokenString?.let {
            builder.addHeader(AUTHORIZATION_HEADER, "$AUTHORIZATION_HEADER_BEARER_PART$it")
//            val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vc2RhcGkuc3VtaWFuLmNvbSIsImlhdCI6MTU0MjI0NzQyNSwiZXhwIjoxNTQ0ODM5NDI1LCJuYmYiOjE1NDIyNDc0MjUsImp0aSI6ImRsZnJ4cFRudHNEbGhNNE8iLCJzdWIiOjMxMzYsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.mCIxx60e8g3_4gI2NfcoQ94KWTeUQ8wVuW5eB5dUsGk"
//            builder.addHeader(AUTHORIZATION_HEADER, "$AUTHORIZATION_HEADER_BEARER_PART$token") // test code
        }

        val response = chain.proceed(builder.build())
        val authorizationHeader = response.header(AUTHORIZATION_HEADER)
        authorizationHeader?.let {
            AppManager.getAccountViewModel().updateToken(newToken(it))
        }
        return response
    }

    private fun newToken(it: String): Token {
        return Token().apply {
            val newTokenString = it.removePrefix(AUTHORIZATION_HEADER_BEARER_PART)
            token = newTokenString
            setExpired_at(0)
            setRefresh_expired_at((System.currentTimeMillis() / 1000L).toInt())
            setUser(AppManager.getAccountViewModel().userInfo)
            is_new = false
        }
    }
}
