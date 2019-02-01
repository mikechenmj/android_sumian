package com.sumian.sddoctor.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/13 19:17
 *     desc   : 提供 主/子线程 执行 runnable 方法
 *     version: 1.0
 *
 *     updated by dq
 *
 *     on  2018/08/31
 * </pre>
 */
class SumianExecutor private constructor() {

    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val mCachedThreadPool: ExecutorService  by lazy {
        Executors.newCachedThreadPool()
    }

    companion object {

        private val INSTANCE: SumianExecutor by lazy {
            SumianExecutor()
        }

        @JvmOverloads
        @JvmStatic
        fun runOnUiThread(runnable: () -> Unit, delay: Long = 0) {
            INSTANCE.mHandler.postDelayed(runnable, delay)
        }

        @JvmOverloads
        @JvmStatic
        fun runOnUiThread(runnable: Runnable, delay: Long = 0) {
            INSTANCE.mHandler.postDelayed(runnable, delay)
        }

        @JvmStatic
        fun runOnBackgroundThread(runnable: () -> Unit) {
            INSTANCE.mCachedThreadPool.execute(runnable)
        }

        @JvmStatic
        fun runOnBackgroundThread(runnable: Runnable) {
            INSTANCE.mCachedThreadPool.execute(runnable)
        }
    }
}