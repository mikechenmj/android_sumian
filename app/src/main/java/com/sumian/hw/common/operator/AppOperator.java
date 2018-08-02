package com.sumian.hw.common.operator;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.sumian.hw.app.HwAppManager;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jzz
 * on 2017/09/30.
 */
public final class AppOperator {
    private static ExecutorService EXECUTORS_INSTANCE;

    public static Executor getExecutor() {
        if (EXECUTORS_INSTANCE == null) {
            synchronized (AppOperator.class) {
                if (EXECUTORS_INSTANCE == null) {
                    EXECUTORS_INSTANCE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
                }
            }
        }
        return EXECUTORS_INSTANCE;
    }

    public static void runOnThread(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    public static GlideUrl getGlideUrlByUser(String url) {
        if (AppManager.getAccountViewModel().isLogin()) {
            return new GlideUrl(url,
                new LazyHeaders
                    .Builder()
                    //.addHeader("Cookie", AccountHelper.getCookie())
                    .build());
        } else {
            return new GlideUrl(url);
        }
    }

}
