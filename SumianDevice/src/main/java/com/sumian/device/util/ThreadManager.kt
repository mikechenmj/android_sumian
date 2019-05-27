package com.sumian.device.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/13 12:00
 * desc   :
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate")
object ThreadManager {
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val mThreadPool = Executors.newCachedThreadPool()

    fun runOnUIThread(runnable: () -> Unit) {
        postToUIThread(runnable, 0)
    }

    fun postToUIThread(runnable: () -> Unit, delay: Long) {
        mMainHandler.postDelayed(runnable, delay)
    }

    fun isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    fun runOnWorkThread(runnable: () -> Unit) {
        mThreadPool.execute(runnable)
    }
}