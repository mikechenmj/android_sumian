package com.sumian.hw.device.pattern

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.SPUtils
import com.sumian.blue.callback.BluePeripheralDataCallback
import com.sumian.blue.model.BluePeripheral
import com.sumian.hw.command.BlueCmd
import com.sumian.hw.log.LogManager
import com.sumian.sd.app.AppManager
import com.sumian.sd.constants.SpKeys

class SyncPatternService : IntentService("SyncPatternService"), BluePeripheralDataCallback {

    override fun onHandleIntent(intent: Intent?) {

    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SyncPatternService::class.java)
            context.startService(intent)
        }
    }

    private fun sendDataToDevice() {
        val bluePeripheral = getConnectedBluePeripheral() ?: return
        bluePeripheral.addPeripheralDataCallback(this)
        bluePeripheral.write("aa4A0510019F2050".toByteArray())
    }

    override fun onSendSuccess(bluePeripheral: BluePeripheral?, data: ByteArray?) {
        LogManager.appendMonitorLog("0x57 主动 turn on 监测仪的监测模式发送指令成功")
    }

    override fun onReceiveSuccess(bluePeripheral: BluePeripheral?, data: ByteArray?) {
        val cmd = BlueCmd.bytes2HexString(data)
        when (cmd) {
            "" -> LogManager.appendMonitorLog("0x57 主动 turn on 监测仪的监测模式发送指令成功")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val bluePeripheral = getConnectedBluePeripheral() ?: return
        bluePeripheral.removePeripheralDataCallback(this)
    }

    private fun getConnectedBluePeripheral(): BluePeripheral? {
        return if (AppManager.getBlueManager().isBluePeripheralConnected) {
            AppManager.getBlueManager().bluePeripheral
        } else {
            null
        }
    }

    private fun persistPattern(data: String) {
        val spUtils = SPUtils.getInstance()
        spUtils.put(SpKeys.DEVICE_PATTERN, data)
    }

    private fun getPersistPattern(): String? {
        return SPUtils.getInstance().getString(SpKeys.DEVICE_PATTERN)
    }

    private fun isPatternAlreadySent(data: String?): Boolean {
        return data != null && data == getPersistPattern()
    }
}
