package com.sumian.app.leancloud;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.OkHttpClient;

/**
 * Created by jzz
 * on 2018/1/7.
 * desc:
 */

public class MyGlideModule implements GlideModule {

    private static Map<String, ProgressListener> mProgressListenerMap;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        mProgressListenerMap = new WeakHashMap<>();
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .addInterceptor(new ProgressInterceptor())
            .build();
        glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(okHttpClient));
    }

    public static void addProgressListener(String url, ProgressListener progressListener) {
        if (TextUtils.isEmpty(url)) return;
        mProgressListenerMap.put(url, progressListener);
    }

    public static ProgressListener getProgressListener(String url) {
        return mProgressListenerMap.get(url);
    }
}
