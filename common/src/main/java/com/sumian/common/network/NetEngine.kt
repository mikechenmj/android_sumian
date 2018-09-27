package com.sumian.common.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.sumian.common.network.interceptor.SpecialRequestInterceptor
import com.sumian.common.network.interceptor.StatusCodeInterceptor
import com.sumian.common.network.interceptor.UAInterceptor
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

class NetEngine<NetApi> private constructor(context: Context, baseUrl: String, clx: Class<out NetApi>, isDebug: Boolean = false, dns: Dns = Dns.SYSTEM, interceptors: Array<out Interceptor>) {

    private var mBaseNetApi: NetApi

    companion object {

        private const val TIMEOUT = 5L

    }

    init {

        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(UAInterceptor.create(context))
                .addInterceptor(StatusCodeInterceptor())
                .dns(dns)
                .addInterceptor(SpecialRequestInterceptor())

        interceptors.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }


        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .callFactory(okHttpClientBuilder.build())
                .build()

        this.mBaseNetApi = retrofit.create(clx)
    }

    fun getNetEngineApi(): NetApi {
        return mBaseNetApi
    }

    class NetEngineBuilder<NetApi> {

        private var mContext: Context? = null

        private var mClx: Class<out NetApi>? = null

        private var mBaseUrl: String? = null

        private var mIsDebug = false

        private var mInterceptors: Array<out Interceptor>? = null

        private var mDns: Dns? = null

        fun with(context: Context): NetEngineBuilder<NetApi> {
            this.mContext = context
            return this
        }

        fun isDebug(isDebug: Boolean): NetEngineBuilder<NetApi> {
            this.mIsDebug = isDebug
            return this
        }

        fun addBaseUrl(baseUrl: String): NetEngineBuilder<NetApi> {
            this.mBaseUrl = baseUrl
            return this
        }

        fun addBaseNetApi(clx: Class<out NetApi>): NetEngineBuilder<NetApi> {
            this.mClx = clx
            return this
        }

        fun addDns(dns: Dns): NetEngineBuilder<NetApi> {
            this.mDns = dns
            return this
        }

        /**
         * you can add many interceptor   e.g. tokenInterceptor
         */
        fun addInterceptor(vararg interceptors: Interceptor): NetEngineBuilder<NetApi> {
            this.mInterceptors = interceptors
            return this
        }

        fun build(): NetEngine<NetApi> {
            return NetEngine(this.mContext!!, this.mBaseUrl!!, this.mClx!!, this.mIsDebug, this.mDns!!, this.mInterceptors!!)
        }

    }

}
