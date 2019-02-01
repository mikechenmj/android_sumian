package com.sumian.sddoctor.network.interceptor

import com.sumian.sddoctor.account.TokenInfo
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.util.SumianExecutor
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

        AppManager.getAccountViewModel().getToken()?.let {
            builder.addHeader(AUTHORIZATION_HEADER, "$AUTHORIZATION_HEADER_BEARER_PART$it")
        }

        val response = chain.proceed(builder.build())
        val authorizationHeader = response.header(AUTHORIZATION_HEADER)
        authorizationHeader?.let {
            SumianExecutor.runOnUiThread({
                AppManager.getAccountViewModel().updateTokenInfo(newToken(it), true)
            })
        }
        return response
    }

    private fun newToken(it: String): TokenInfo {
        val newTokenString = it.removePrefix(AUTHORIZATION_HEADER_BEARER_PART)
        return TokenInfo(newTokenString, 0, (System.currentTimeMillis() / 1000L).toInt())
    }
}
