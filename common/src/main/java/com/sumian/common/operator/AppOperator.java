package com.sumian.common.operator;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jzz
 * on 2017/09/30.
 */
public final class AppOperator {

    private static ExecutorService EXECUTORS;

    public static ExecutorService getExecutor() {
        if (EXECUTORS == null) {
            synchronized (AppOperator.class) {
                if (EXECUTORS == null) {
                    EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
                }
            }
        }
        return EXECUTORS;
    }

    public static void runOnThread(Runnable runnable) {
        getExecutor().execute(runnable);
    }
}
