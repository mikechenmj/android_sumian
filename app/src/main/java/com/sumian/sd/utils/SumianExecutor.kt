package com.sumian.sd.utils

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
class SumianExecutor private constructor() {

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mCachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    companion object {
        val instance: SumianExecutor = SumianExecutor()


        fun runOnUiThread(runnable: Runnable) {
            instance.mHandler.postDelayed(runnable, 0)
        }

        fun runOnUiThread(runnable: () -> Unit, delay: Long = 0) {
            instance.mHandler.postDelayed(runnable, delay)
        }

        fun runOnUiThread(runnable: Runnable, delay: Long = 0) {
            instance.mHandler.postDelayed(runnable, delay)
        }

        fun runOnBackgroundThread(runnable: () -> Unit) {
            instance.mCachedThreadPool.execute(runnable)
        }

        fun runOnBackgroundThread(runnable: Runnable) {
            instance.mCachedThreadPool.execute(runnable)
        }
    }
}