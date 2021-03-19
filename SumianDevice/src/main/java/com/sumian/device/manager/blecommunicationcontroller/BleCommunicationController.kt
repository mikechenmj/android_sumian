package com.sumian.device.manager.blecommunicationcontroller

import android.os.Handler
import android.os.Looper
import androidx.collection.ArrayMap
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.callback.WriteBleDataCallback
import com.sumian.device.cmd.BleConstants
import com.sumian.device.cmd.BleConstants.Companion.SYNC_TRANSPARENT
import com.sumian.device.manager.DeviceManager
import com.sumian.device.util.LogManager
import com.sumian.device.util.ThreadManager
import java.lang.ref.SoftReference

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/13 11:37
 * desc   : 设备数据读写操作类。读写操作分几类，纯写，请求响应，纯读（接收设备推送）
 * version: 1.0
 */
object BleCommunicationController {
    private const val SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
    private const val WRITE_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
    private const val NOTIFY_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
    private const val DESCRIPTORS_UUID = "00002902-0000-1000-8000-00805f9b34fb"
    private const val TIMEOUT_DURATION = 3_000L
    private const val RETRY_REQUEST_CMD_TIMES = 3

    private val mRetryTimesMap: ArrayMap<String, Int> = ArrayMap()
    private var mDevice: BleDevice? = null
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val mResponseMap = HashMap<String, SoftReference<BleRequestCallback>>()
    private val mTimeoutCallbacks = LinkedHashMap<String, Runnable>()
    val mBleCommunicationWatcherList = ArrayList<BleCommunicationWatcher>()
    val mOriginBleCommunicationWatcher = object : BleCommunicationWatcher {
        override fun onRead(
                data: ByteArray,
                hexString: String
        ) {
            notifyWatchers(true, data, hexString)
        }

        override fun onWrite(
                data: ByteArray,
                hexString: String,
                success: Boolean,
                errorMsg: String?
        ) {
            notifyWatchers(false, data, hexString, success, errorMsg)
        }

        private fun notifyWatchers(
                isRead: Boolean,
                data: ByteArray,
                hexString: String,
                success: Boolean = true,
                errorMsg: String? = null
        ) {
            if (success) {
                if (hexString.length > 3) {
                    if (hexString.substring(2, 4) != SYNC_TRANSPARENT) {
                        LogManager.log("Ble Data", "${if (isRead) "D" else "A"}: $hexString")
                    }
                } else {
                    LogManager.log("Ble Data 错误长度", "${if (isRead) "D" else "A"}: $hexString")
                }
            } else if (!success) {
                LogManager.bleFlowLog("$hexString 写入失败, errorMsg: $errorMsg")
            }
            val iterator = mBleCommunicationWatcherList.iterator()
            while (iterator.hasNext()) {
                val listener = iterator.next()
                if (isRead) {
                    listener.onRead(data, hexString)
                } else {
                    listener.onWrite(data, hexString, success, errorMsg)
                }
            }
        }
    }

    fun registerBleCommunicationWatcher(watcher: BleCommunicationWatcher) {
        if (!mBleCommunicationWatcherList.contains(watcher)) {
            mBleCommunicationWatcherList.add(watcher)
        }
    }

    fun unregisterBleCommunicationWatcher(watcher: BleCommunicationWatcher) {
        val iterator = mBleCommunicationWatcherList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == watcher) {
                iterator.remove()
            }
        }
    }

    fun init() {
        registerBleCommunicationWatcher(mBleCommunicationWatcherForResponse)
    }

    private val mBleCommunicationWatcherForResponse = object : BleCommunicationWatcher {
        override fun onRead(data: ByteArray, hexString: String) {
            val dataString = HexUtil.formatHexString(data)
            if (dataString.length < 4) {
                return
            }
            val cmd = dataString.substring(2, 4)
            makeResponse(cmd, true, data)
        }
    }

    private fun makeResponse(
            cmd: String,
            success: Boolean,
            data: ByteArray? = null,
            errorCode: Int? = null,
            errorMsg: String? = null
    ) {
        val callback = mResponseMap[cmd]?.get()
        if (success) {
            callback?.onResponse(data!!, HexUtil.formatHexString(data))
        } else {
            callback?.onFail(errorCode!!, errorMsg!!)
        }
        removeResponseCallback(cmd)
        removeTimeoutCallback(cmd)
    }

    /**
     * 发起请求，会受到设备的响应或超时回调
     */
    fun requestByCmd(cmd: String, callback: BleRequestCallback? = null, retry: Boolean = false) {
        requestWithRetry(HexUtil.hexStringToBytes(BleConstants.APP_CMD_HEADER + cmd), callback, retry)
    }

    fun requestWithRetry(data: ByteArray, callback: BleRequestCallback?, retry: Boolean = false) {
        val cmd = HexUtil.formatHexString(data).substring(2, 4)
        mRetryTimesMap[cmd] = 0
        var retryCallback = object : BleRequestCallback {
            override fun onResponse(data: ByteArray, hexString: String) {
                LogManager.bleRequestStatusLog("请求蓝牙状态成功,cmd: $cmd retry: $retry")
                callback?.onResponse(data, hexString)
            }

            override fun onFail(code: Int, msg: String) {
                LogManager.bleRequestStatusLog("请求蓝牙状态失败，cmd:$cmd code: $code msg: $msg retry: $retry")
                if (retry) {
                    if (mRetryTimesMap[cmd]!! < RETRY_REQUEST_CMD_TIMES) {
                        ThreadManager.postToUIThread({ request(data, cmd, this) }, DeviceManager.WRITE_DATA_INTERVAL)
                        mRetryTimesMap[cmd] = mRetryTimesMap[cmd]!! + 1
                        LogManager.bleRequestStatusLog("同步状态失败且重试第${mRetryTimesMap[cmd]}次")
                    } else {
                        callback?.onFail(code, msg)
                    }
                } else {
                    callback?.onFail(code, msg)
                }
            }
        }
        request(data, cmd, retryCallback)
    }

    fun request(data: ByteArray, cmd: String, callback: BleRequestCallback?) {
        putResponseCallbackToMap(cmd, callback)
        postTimeoutCallbackDelay(cmd, Runnable {
            makeResponse(
                    cmd, false, null, BleRequestCallback.ERROR_CODE_TIMEOUT, "timeout"
            )
        })
        writeData(data, 0, object : WriteBleDataCallback {
            override fun onSuccess(data: ByteArray) {
            }

            override fun onFail(code: Int, msg: String) {
                makeResponse(cmd, false, null, code, msg)
            }
        })
    }

    /**
     * 向设备写数据，回调为蓝牙层写成功/失败的回调
     */
    fun writeData(data: ByteArray, delay: Long = 0, callback: WriteBleDataCallback? = null) {
        val hexString = HexUtil.formatHexString(data)
        mMainHandler.postDelayed({
            BleManager.getInstance().write(
                    mDevice,
                    SERVICE_UUID,
                    WRITE_UUID,
                    data,
                    object : BleWriteCallback() {
                        override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                            callback?.onSuccess(data)
                            mOriginBleCommunicationWatcher.onWrite(
                                    data,
                                    hexString,
                                    true,
                                    null
                            )
                        }

                        override fun onWriteFailure(exception: BleException?) {
                            mOriginBleCommunicationWatcher.onWrite(
                                    data,
                                    hexString,
                                    false,
                                    exception?.description
                            )
                            callback?.onFail(1, exception?.description ?: "error unknown")
                        }
                    })
        }, delay)
    }

    private fun postTimeoutCallbackDelay(cmd: String, runnable: Runnable) {
        mTimeoutCallbacks[cmd] = runnable
        mMainHandler.postDelayed(runnable, TIMEOUT_DURATION)
    }

    private fun removeTimeoutCallback(cmd: String) {
        mMainHandler.removeCallbacks(mTimeoutCallbacks[cmd])
    }

    private fun putResponseCallbackToMap(cmd: String, callback: BleRequestCallback?) {
        if (callback == null) {
            return
        }
        mResponseMap[cmd] = SoftReference(callback)
    }

    private fun removeResponseCallback(cmd: String) {
        mResponseMap.remove(cmd)
    }

    fun startListenDeviceNotification(device: BleDevice?) {
        mDevice = device
        BleManager.getInstance().notify(
                mDevice,
                SERVICE_UUID,
                NOTIFY_UUID,
                object : BleNotifyCallback() {
                    override fun onCharacteristicChanged(data: ByteArray?) {
                        if (data == null)  {
                            LogManager.bleFlowLog("监测仪传的数据为空")
                            return
                        }
                        val formatData = HexUtil.formatHexString(data)
                        if (formatData == null) {
                            LogManager.bleFlowLog("监测仪传的数据格式化为空以及监测仪传的数据长度为: ${data.size}")
                            return
                        }
                        mOriginBleCommunicationWatcher.onRead(
                                data, formatData

                        )
                    }

                    override fun onNotifyFailure(exception: BleException?) {
                        LogManager.log(exception.toString())
                    }

                    override fun onNotifySuccess() {
                    }
                })
    }
}
