package com.sumian.sleepdoctor.network.engine

import com.google.gson.GsonBuilder
import com.sumian.sleepdoctor.BuildConfig
import com.sumian.sleepdoctor.network.api.DoctorApi
import com.sumian.sleepdoctor.network.interceptor.AuthInterceptor
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

class NetEngine {

    private val mDoctorApi: DoctorApi

    companion object {
        private const val TIMEOUT = 5
    }

    init {

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(AuthInterceptor.create())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .callFactory(okHttpClient)
                .build()

        this.mDoctorApi = retrofit.create(DoctorApi::class.java)

    }

    fun httpRequest(): DoctorApi {
        return mDoctorApi
    }


}
