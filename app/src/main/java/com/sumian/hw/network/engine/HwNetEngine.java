package com.sumian.hw.network.engine;

import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.api.SleepyV1Api;
import com.sumian.hw.network.interceptor.AuthInterceptor;
import com.sumian.sleepdoctor.BuildConfig;

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

public class HwNetEngine {

    private static final int TIMEOUT = 3;
    private SleepyApi mSleepyApi;

    private SleepyV1Api mSleepyV1Api;

    public HwNetEngine() {

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
                .baseUrl(BuildConfig.HW_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
