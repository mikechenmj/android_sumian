package com.sumian.device

import org.junit.Test
import java.util.concurrent.Executors

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/16 14:39
 * desc   :
 * version: 1.0
 */
class KotlinTest {
    private val mThreadPool = Executors.newCachedThreadPool()


    //    fun runOnWorkThread(runnable: () -> Unit) {
//        mThreadPool.execute(Runnable(runnable))
//    }
    fun runOnWorkThread(runnable: () -> Unit) {
        mThreadPool.execute(runnable)
    }

    fun runOnWorkThread(runnable: Runnable) {
        mThreadPool.execute(runnable)
    }

    @Test
    fun test() {
//        runOnWorkThread(
//            Runnable { println(Thread.currentThread()) }
//        )
        runOnWorkThread { println(Thread.currentThread()) }

    }
}