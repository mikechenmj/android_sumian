package com.sumian.common.log

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/3 10:47
 * desc   :
 * version: 1.0
 */
object CommonLog {
    var mLog: ILog? = null

    fun log(vararg arr: String) {
        val sb = StringBuilder()
        for (s in arr) {
            sb.append(s)
        }
        mLog?.log((getStackTraceElement()?.toString() ?: "") + "\n" + sb.toString())
    }

    private fun getStackTraceElement(): StackTraceElement? {
        val stackTrace = Throwable().stackTrace
        val stackIndex = 3
        if (stackIndex >= stackTrace.size) {
            return null
        }
        return stackTrace[stackIndex]
    }
}