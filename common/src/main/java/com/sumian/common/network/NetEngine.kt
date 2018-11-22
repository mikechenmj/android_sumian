package com.sumian.common.network

import com.google.gson.GsonBuilder
import com.sumian.common.network.interceptor.SpecialRequestInterceptor
import com.sumian.common.network.interceptor.StatusCodeInterceptor
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by jzz
 * on 2018/1/15.
 * desc: 网络引擎
 */

class NetEngine private constructor(baseUrl: String, isDebug: Boolean = false, dns: Dns = Dns.SYSTEM, interceptors: Array<out Interceptor>) {

    companion object {

        private const val TIMEOUT = 5L

    }

    private val mRetrofit by lazy {

        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .protocols(arrayListOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .dns(dns)
                .addInterceptor(HttpLoggingInterceptor().setLevel(if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(StatusCodeInterceptor.create())
                .addInterceptor(SpecialRequestInterceptor.create())

        interceptors.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        return@lazy Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .callFactory(okHttpClientBuilder.build())
                .build()
    }

    /**
     * 可以使用该函数获取相同 base_url 下的 retrofit e.g. 按模块去实例化不同网络请求 Api
     */
    fun getInstalledRetrofit(): Retrofit {
        return mRetrofit
    }

    /**
     * 可以使用该 函数 创建一个网络请求 Api
     */
    fun <NetworkApi> create(clx: Class<out NetworkApi>): NetworkApi {
        return mRetrofit.create(clx)
    }

    class NetEngineBuilder {

        private lateinit var mBaseUrl: String

        private var mIsDebug = false

        private var mInterceptors: Array<out Interceptor>? = null

        private var mDns: Dns = Dns.SYSTEM

        fun isDebug(isDebug: Boolean = false): NetEngineBuilder {
            this.mIsDebug = isDebug
            return this
        }

        fun baseUrl(baseUrl: String): NetEngineBuilder {
            this.mBaseUrl = baseUrl
            return this
        }

        fun dns(dns: Dns = Dns.SYSTEM): NetEngineBuilder {
            this.mDns = dns
            return this
        }

        /**
         * you can add many interceptor   e.g. tokenInterceptor
         */
        fun addInterceptor(vararg interceptors: Interceptor): NetEngineBuilder {
            this.mInterceptors = interceptors
            return this
        }

        fun build(): NetEngine {
            return NetEngine(this.mBaseUrl, this.mIsDebug, this.mDns, this.mInterceptors!!)
        }

    }

}
