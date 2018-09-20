package com.sumian.sd.network.interceptor

import android.text.TextUtils
import android.webkit.WebSettings
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:
 */

class AuthInterceptor private constructor() : Interceptor {

    companion object {

        fun create(): AuthInterceptor {
            return AuthInterceptor()
        }

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

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
                .request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "$userAgent Sumian-Doctor-Android")

        val token = AppManager.getAccountViewModel().getTokenString()
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("Authorization", "Bearer " + token)
        }
        return chain.proceed(builder.build())
    }

}
