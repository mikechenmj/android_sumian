package com.sumian.app.base;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface BasePresenter {

    Handler mMainHandler = new Handler(Looper.getMainLooper());//主线程切换

    ArrayList<Call> mCalls = new ArrayList<>();

    default void release() {
        if (mCalls.isEmpty()) return;
        for (Call call : mCalls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        mCalls.clear();
    }

    default void runUiThread(Runnable run) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mMainHandler.post(run);
        } else {
            run.run();
        }
    }
}
