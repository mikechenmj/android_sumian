package com.sumian.sd.network.interceptor

import com.sumian.sd.app.AppManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:token 认证鉴权拦截器
 */

class TokenAuthInterceptor private constructor() : Interceptor {

    companion object {

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
            builder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(builder.build())
    }
}
