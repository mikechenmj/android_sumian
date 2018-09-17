package com.sumian.hw.device.pattern

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import com.sumian.blue.callback.BluePeripheralDataCallback
import com.sumian.blue.model.BluePeripheral
import com.sumian.common.network.response.BaseResponseCallback
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.hw.command.BlueCmd
import com.sumian.hw.log.LogManager
import com.sumian.sd.app.AppManager
import com.sumian.sd.constants.SpKeys

/**
 * 该类用于从服务器下拉pattern数据同步到设备
 * 大致流程如下：
 * 1. 从服务器拉取 pattern 信息
 * 2. 校验 最新的pattern 信息 是否和上次同步成功的数据相同，如果相同则结束
 * 3. 将 最新的pattern 发送给 设备，全部收到 成功回执后，将pattern持久化下来，用于下次做校验
 */
class SyncPatternService : Service(), BluePeripheralDataCallback {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private var mPatternValues = ArrayList<String>()
    private val mSuccessReceivedPattern = HashSet<String>()
    private var mResponseCount = 0  // 发送 pattern 设备回执次数，d当回执次数等于发送次数时，通讯结束，取消监听
    private val mHandler = Handler()

    companion object {
        private const val UNREGISTER_BLUE_DATA_CALLBACK_DELAY = 5000L

        fun start(context: Context) {
            val intent = Intent(context, SyncPatternService::class.java)
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        queryPattern()
    }

    fun queryPattern() {
        LogUtils.d()
        val call = AppManager.getHttpService().getUserPattern()
        call.enqueue(object : BaseResponseCallback<List<PatternData>>() {
            override fun onSuccess(response: List<PatternData>?) {
                LogUtils.d(response)
                response?.apply {
                    val cmdList = ArrayList<ByteArray>()
                    mPatternValues.clear()
                    mSuccessReceivedPattern.clear()
                    for (data in response) {
                        val value = data.value
                        val cmd = BlueCmd.makePatternCmd(value)
                        mPatternValues.add(value)
                        cmdList.add(cmd)
                    }
                    val patternAlreadySent = isPatternAlreadySent(mPatternValues)
                    if (!patternAlreadySent) {
                        sendDataToDevice(cmdList)
                    } else {
                        stopSelf()
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse)
            }
        })
    }

    private fun sendDataToDevice(data: ArrayList<ByteArray>) {
        mResponseCount = 0
        val bluePeripheral = getConnectedBluePeripheral() ?: return
        bluePeripheral.addPeripheralDataCallback(this)
        mHandler.postDelayed({ stop() }, UNREGISTER_BLUE_DATA_CALLBACK_DELAY)    // 设备回执超时
        var delay = 0L
        for (bytes in data) {
            bluePeripheral.writeDelay(bytes, delay)
            delay += 200
            LogUtils.d("write bytes: ", BlueCmd.bytes2HexString(bytes))
        }
    }

    override fun onSendSuccess(bluePeripheral: BluePeripheral?, data: ByteArray?) {
        if (isPatternCmd(data)) {
            LogManager.appendMonitorLog("0x4a pattern数据 APP 发送成功")
        }
    }

    /**
     * 55 4a 03 1001 88
     */
    override fun onReceiveSuccess(bluePeripheral: BluePeripheral?, bytes: ByteArray?) {
        if (isPatternCmd(bytes)) {
            val data = BlueCmd.bytes2HexString(bytes)
            LogUtils.d(data)
            if (data.endsWith("88")) {
                val patternIndex = data.substring(6, 10)
                mSuccessReceivedPattern.add(patternIndex)
                if (mSuccessReceivedPattern.size == mPatternValues.size) {
                    LogManager.appendMonitorLog("0x4a pattern数据 监测仪 接收完毕")
                    persistPattern(JsonUtil.toJson(mPatternValues))
                }
            }
            mResponseCount++
            if (mResponseCount == mPatternValues.size) {
                stop()
            }
        }
    }

    private fun isPatternCmd(bytes: ByteArray?): Boolean {
        val data = BlueCmd.bytes2HexString(bytes)
        if (data == null || data.length < 4) {
            return false
        }
        return data.startsWith("554a")
    }

    private fun stop() {
        mHandler.removeCallbacksAndMessages(null)
        unregisterBlueDataCallback()
        stopSelf()
    }

    private fun unregisterBlueDataCallback() {
        getConnectedBluePeripheral()?.removePeripheralDataCallback(this)
    }

    private fun getConnectedBluePeripheral(): BluePeripheral? {
        return if (AppManager.getBlueManager().isBluePeripheralConnected) {
            AppManager.getBlueManager().bluePeripheral
        } else {
            null
        }
    }

    private fun persistPattern(data: String) {
        SPUtils.getInstance().put(SpKeys.DEVICE_PATTERN, data)
    }

    private fun getPersistPattern(): List<String>? {
        val json = SPUtils.getInstance().getString(SpKeys.DEVICE_PATTERN)
        return JsonUtil.fromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
    }

    private fun isPatternAlreadySent(data: List<String>?): Boolean {
        return data != null && data == getPersistPattern()
//        return false
    }
}
