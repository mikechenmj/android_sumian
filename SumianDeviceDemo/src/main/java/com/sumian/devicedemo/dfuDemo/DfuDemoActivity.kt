package com.sumian.devicedemo.dfuDemo

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.clj.fastble.utils.HexUtil
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.callback.ScanCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.DeviceType
import com.sumian.device.dfu.DfuUpgradeHelper
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.DeviceStateHelper
import com.sumian.device.util.Cmd
import com.sumian.device.util.CmdQueue
import com.sumian.device.util.LogManager
import com.sumian.device.util.MacUtil
import com.sumian.devicedemo.R
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import java.util.zip.CRC32
import kotlin.coroutines.CoroutineContext

class DfuDemoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dfu_demo)
    }


    fun enterDfuAndUpdate(v: View) {
        val coroutines = CoroutineScope(Dispatchers.Main)
        coroutines.launch {
            val mac = queryMac(coroutineContext)
            if (mac == CHANNEL_FAILED.toLong()) {
                return@launch
            }
            val enterSuccess = enterDfuMode(coroutines) == CHANNEL_SUCCESS
            Log.i("MCJ", "进入 dfu 模式: $enterSuccess")
            if (!enterSuccess) {
                return@launch
            }
            withTimeoutOrNull(30000) {
                val dfuAddress = scanDfuDevice(coroutines, mac)
                Log.i("MCJ", "扫描到 dfu 设备: $dfuAddress")
                DfuUpdateHelper(this@DfuDemoActivity, dfuAddress).start()
            }
        }
    }

    fun findDfuAndUpdate(v: View) {
        val coroutines = CoroutineScope(Dispatchers.Main)
        coroutines.launch {
            val mac = queryMac(coroutineContext)
            if (mac == CHANNEL_FAILED.toLong()) {
                return@launch
            }
            withTimeoutOrNull(30000) {
                val dfuAddress = scanDfuDevice(coroutines)
                Log.i("MCJ", "扫描到 dfu 设备: $dfuAddress")
                DfuUpdateHelper(this@DfuDemoActivity, dfuAddress).start()
            }
        }
    }

    private suspend fun scanDfuDevice(coroutines: CoroutineScope, mac: Long = 0): String {
        val channel = Channel<String>(CONFLATED)
        DeviceManager.scanDelay(object : ScanCallback {
            override fun onStart(success: Boolean) {
            }

            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                val isDeviceVersionValid = DfuUpgradeHelper.isLeScanDeviceVersionValid(DeviceType.MONITOR,
                        device.name ?: "", scanRecord)
                if (isDeviceVersionValid) {
                    if (mac.toInt() == 0 || MacUtil.getLongMacFromStringMac(device.address) - 1 == mac) {
                        DeviceManager.stopScan()
                        coroutines.launch {
                            channel.send(device.address)
                            channel.close()
                        }
                    }
                }
            }

            override fun onStop() {
            }
        })
        return channel.receive()
    }

    private suspend fun enterDfuMode(coroutines: CoroutineScope): Int {
        val channel = Channel<Int>(CONFLATED)
        DfuUpgradeHelper.enterDfuMode(
                DeviceType.MONITOR,
                {
                    coroutines.launch(Dispatchers.IO) {
                        channel.send(CHANNEL_SUCCESS)
                        channel.close()
                    }
                },
                { code, msg ->
                    coroutines.launch(Dispatchers.IO) {
                        channel.send(CHANNEL_FAILED)
                        channel.close()
                    }
                })
        return channel.receive()
    }

    private suspend fun queryMac(coroutineContext: CoroutineContext) = withContext(coroutineContext) {
        val channel = Channel<Long>(CONFLATED)
        DfuUpgradeHelper.queryTargetDeviceMac(
                DeviceType.MONITOR,
                onSuccess = {
                    launch {
                        channel.send(it)
                        channel.close()
                    }
                },
                onFail = { code, msg ->
                    launch {
                        channel.send(CHANNEL_FAILED.toLong())
                        channel.close()
                    }
                })
        channel.receive()
    }
}

suspend fun <T : Any> Channel<in Any>.sendAndClose(e: T) {
    send(e)
    close()
}