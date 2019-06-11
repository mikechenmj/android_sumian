package com.sumian.device.util

object LogManager {

    private const val TAG_DEFAULT = "LogManager"
    private const val TAG_MONITOR = "监测仪"
    private const val TAG_SLEEP_MASTER = "速眠仪"
    private const val TAG_TRANSPARENT_DATA = "透传数据"
    private const val TAG_UPLOAD_SLEEP_DATA = "上传数据"

    private var sLogger: ILogger? = null

    fun log() {

    }

    fun log(log: String?) {
        log(TAG_DEFAULT, log)
    }

    fun monitorLog(log: String?) {
        log(TAG_MONITOR, log)
    }

    fun sleepMasterLog(log: String?) {
        log(TAG_SLEEP_MASTER, log)
    }

    fun transparentLog(log: String?) {
        log(TAG_TRANSPARENT_DATA, log)
    }

    fun uploadSleepDatatLog(log: String?) {
        log(TAG_UPLOAD_SLEEP_DATA, log)
    }

    fun log(tag: String?, log: String?) {
        sLogger?.log(tag ?: TAG_DEFAULT, log ?: "")
    }

    fun setLogger(logger: ILogger) {
        sLogger = logger
    }

}

interface ILogger {
    fun log(tag: String, log: String)
}
