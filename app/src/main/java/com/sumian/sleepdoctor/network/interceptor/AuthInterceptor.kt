package com.sumian.sleepdoctor.network.interceptor

import android.webkit.WebSettings
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.app.AppManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:
 */

class AuthInterceptor private constructor() : Interceptor {

    companion object {

        // private val TAG = AuthInterceptor::class.java.simpleName

        fun create(): AuthInterceptor {
            return AuthInterceptor()
        }
    }

    private val userAgent: String
        get() {
            val userAgent: String = try {
                WebSettings.getDefaultUserAgent(App.getAppContext())
            } catch (e: Exception) {
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
            // Log.e(TAG, "getUserAgent: ------>" + sb.toString());
            return sb.toString()
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain
                .request()
                .newBuilder()
                .addHeader("Accept", "application/vnd.sumianapi+json")
                .addHeader("Content-Type", "application/json")
                .header("User-Agent", userAgent)
                .addHeader("Authorization", "Bearer " + AppManager.getAccountViewModel().accessToken())
                .build()

        return chain.proceed(request)
    }

}
