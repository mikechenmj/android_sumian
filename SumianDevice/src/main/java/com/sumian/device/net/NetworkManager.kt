package com.sumian.device.net

import android.text.TextUtils
import com.sumian.device.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 14:31
 * desc   :
 * version: 1.0
 */
object NetworkManager {

    private lateinit var mApi: Api

    fun init(baseUrl: String) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw IllegalArgumentException("base url not set")
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(TokenInterceptor())
                .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        mApi = retrofit.create<Api>(Api::class.java)
    }

    fun getApi(): Api {
        return mApi
    }
}