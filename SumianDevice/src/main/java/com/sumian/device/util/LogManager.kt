package com.sumian.device.util

import android.util.Log
import java.util.concurrent.*

object LogManager {

    private const val TAG_DEFAULT = "LogManager"
    private const val TAG_BLE_SDK = "蓝牙SDK"
    private const val TAG_SCAN_DEVICE = "蓝牙扫描"
    private const val TAG_CONNECT_DEVICE = "蓝牙连接"
    private const val TAG_REQUEST_STATUS_DEVICE = "请求蓝牙状态"
    private const val TAG_SYNC_FLOW = "同步数据流程"
    private const val TAG_MONITOR = "监测仪"
    private const val TAG_SLEEP_MASTER = "速眠仪"
    private const val TAG_TRANSPARENT_DATA = "透传数据"
    private const val TAG_UPLOAD_SLEEP_DATA = "上传数据"
    private const val TAG_DEVICE_UPGRADE = "固件升级"

    private var sLogger: ILogger? = null

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

    fun uploadSleepDataLog(log: String?) {
        log(TAG_UPLOAD_SLEEP_DATA, log)
    }

    fun deviceUpgradeLog(log: String?) {
        log(TAG_DEVICE_UPGRADE, log)
    }

    fun bleSdkLog(log: String?) {
        log(TAG_BLE_SDK, log)
    }

    fun bleScanLog(log: String?) {
        log(TAG_SCAN_DEVICE, log)
    }

    fun bleConnectLog(log: String?) {
        log(TAG_CONNECT_DEVICE, log)
    }

    fun bleRequestStatusLog(log: String?) {
        log(TAG_REQUEST_STATUS_DEVICE, log)
    }

    fun bleFlowLog(s: String) {
        log(TAG_SYNC_FLOW, s)
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
