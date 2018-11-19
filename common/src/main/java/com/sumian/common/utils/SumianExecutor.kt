package com.sumian.common.utils

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
 * </pre>
 */
object SumianExecutor {

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mCachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    @JvmOverloads
    fun runOnUiThread(runnable: Runnable, delay: Long = 0) {
        mHandler.postDelayed(runnable, delay)
    }

    @JvmOverloads
    fun runOnUiThread(runnable: () -> Unit, delay: Long = 0) {
        mHandler.postDelayed(runnable, delay)
    }

    fun runOnBackgroundThread(runnable: () -> Unit) {
        mCachedThreadPool.execute(runnable)
    }

    fun runOnBackgroundThread(runnable: Runnable) {
        mCachedThreadPool.execute(runnable)
    }
}