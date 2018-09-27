package com.sumian.common.network.interceptor

import android.content.Context
import android.webkit.WebSettings
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.ref.WeakReference

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:
 */

class UAInterceptor private constructor(context: Context) : Interceptor {

    private val mWeakReference: WeakReference<Context> = WeakReference(context)

    companion object {
        fun create(context: Context): Interceptor {
            return UAInterceptor(context)
        }
    }

    private val userAgent: String
        get() {
            val userAgent: String = try {
                WebSettings.getDefaultUserAgent(mWeakReference.get())
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
            // Log.e(TAG, "getUserAgent: ------>" + sb.toString());
            return sb.toString()
        }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain
                .request()
                .newBuilder()
                .addHeader("Accept", "application/vnd.sumianapi+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "$userAgent Sumian-Doctor-Android")
                .build()

        return chain.proceed(request)
    }

}
