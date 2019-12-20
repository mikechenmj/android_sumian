package com.sumian.common.log

import android.content.Context
import xcrash.TombstoneParser
import xcrash.XCrash

object CrashLogSender {

    var callback: CrashCallback? = null
    var javaCallback: CrashCallback? = null
    var anrCallback: CrashCallback? = null
    var nativeCallback: CrashCallback? = null

    fun listen(context: Context, crashCallback: CrashCallback? = callback) {
        XCrash.init(context, XCrash.InitParameters()
                .setJavaCallback { logPath, emergency ->
                    var callback = javaCallback ?: crashCallback
                    callback?.onCrash(logPath, emergency)
                }
                .setAnrCallback { logPath, emergency ->
                    var callback = anrCallback ?: crashCallback
                    callback?.onCrash(logPath, emergency)
                }
                .setNativeCallback { logPath, emergency ->
                    var callback = nativeCallback ?: crashCallback
                    callback?.onCrash(logPath, emergency)
                }
        )
    }

    fun getNormalCrashLog(logPath: String?, emergency: String?): String {
        var keyBacktrace = TombstoneParser.parse(logPath, emergency)[TombstoneParser.keyBacktrace]
        var keyJavaStacktrace = TombstoneParser.parse(logPath, emergency)[TombstoneParser.keyJavaStacktrace]
        var keyAbortMessage = TombstoneParser.parse(logPath, emergency)[TombstoneParser.keyAbortMessage]
        var keyXCrashError = TombstoneParser.parse(logPath, emergency)[TombstoneParser.keyXCrashError]
        var keyMemoryInfo = TombstoneParser.parse(logPath, emergency)[TombstoneParser.keyMemoryInfo]

        fun String.appendIfNotNull(str: String?): String {
            if (str != null) {
                return this + "\n" + str
            }
            return this
        }

        return "".appendIfNotNull(keyBacktrace).appendIfNotNull(keyJavaStacktrace)
                .appendIfNotNull(keyAbortMessage).appendIfNotNull(keyXCrashError)
                .appendIfNotNull(keyMemoryInfo)
    }

    interface CrashCallback {
        @Throws(Exception::class)
        fun onCrash(logPath: String?, emergency: String?)
    }
}