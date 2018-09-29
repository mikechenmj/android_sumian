package com.sumian.sd.network.interceptor

import android.webkit.WebSettings
import com.sumian.hw.common.util.SystemUtil
import com.sumian.sd.app.App
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:  一般http/https 请求常规拦截器  包括 为请求添加UA/Host/Accept-Language/content-Type...
 */

class NormalInterceptor private constructor() : Interceptor {

    companion object {

        @JvmStatic
        fun create(): NormalInterceptor {
            return NormalInterceptor()
        }

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
                .request()
                .newBuilder()
                .addHeader("Accept-Language", SystemUtil.getSystemLanguage())
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "$userAgent Sumian-Doctor-Android")
                .addHeader("Host", chain.request().url().newBuilder().build().host())

        return chain.proceed(builder.build())
    }

    private val userAgent: String
        get() {
            val userAgent: String = try {
                WebSettings.getDefaultUserAgent(App.getAppContext())
            } catch (e: Exception) {
                e.printStackTrace()
                System.getProperty("http.agent")
            }

            val sb = StringBuilder()
            var i = 0
            val length = userAgent.length
            while (i < length) {
                val c = userAgent[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", c.toInt()))
                } else {
                    sb.append(c)
                }
                i++
            }
            return sb.toString()
        }

}
