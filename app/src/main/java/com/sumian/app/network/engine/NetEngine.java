package com.sumian.app.network.engine;

import com.google.gson.GsonBuilder;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.app.network.api.SleepyApi;
import com.sumian.app.network.api.SleepyV1Api;
import com.sumian.app.network.interceptor.AuthInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public class NetEngine {

    private static final int TIMEOUT = 3;
    private SleepyApi mSleepyApi;

    private SleepyV1Api mSleepyV1Api;

    public NetEngine() {

        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        protocols.add(Protocol.HTTP_2);

        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .protocols(protocols)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(AuthInterceptor.create())
                .build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
//            .addConverterFactory(FastJsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .callFactory(okHttpClient)
                .build();

        this.mSleepyApi = retrofit.create(SleepyApi.class);
        this.mSleepyV1Api = retrofit.create(SleepyV1Api.class);
    }

    public SleepyApi getHttpService() {
        return mSleepyApi;
    }

    public SleepyV1Api getV1HttpService() {
        return mSleepyV1Api;
    }

}
