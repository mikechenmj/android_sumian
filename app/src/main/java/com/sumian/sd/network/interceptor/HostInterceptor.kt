package com.sumian.sd.network.interceptor

import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by dq
 *
 * on 2018/9/25
 *
 * desc: host 拦截器  可以动态替换下发的 ip 直连,不过如果是 https 请求  还需要处理证书问题   或者我们可以使用 默认直接替换 dns
 */
class HostInterceptor private constructor() : Interceptor {

    companion object {

        @JvmStatic
        fun create(): HostInterceptor {
            return HostInterceptor()
        }

    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val httpUrl = original.url().newBuilder().host(AppManager.getHttpDns().getBaseUrlFromHostIp(BuildConfig.HW_BASE_URL)).build()

        val completeRequest = original.newBuilder()
                .url(httpUrl)
                .addHeader("Host", httpUrl.host())
                .build()

        val builder = completeRequest.newBuilder()

        return chain.proceed(builder.build())
    }
}