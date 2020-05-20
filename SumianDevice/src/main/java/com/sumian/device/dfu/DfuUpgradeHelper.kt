package com.sumian.device.dfu

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.clj.fastble.utils.HexUtil
import com.sumian.device.R
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.callback.ScanCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.DeviceType
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.util.*
import no.nordicsemi.android.dfu.*
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/9 10:26
 * desc   :
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object DfuUpgradeHelper {
    private var mDfuServiceController: DfuServiceController? = null
    private var mApplicationContext: Context? = DeviceManager.mApplication

    private const val DELAY_RECONNECT_DEVICE = 3000L
    const val ERROR_CODE_ENTER_DFU_MODE_FAIL = 0
    const val ERROR_CODE_DFU_ABORTED = 1
    const val ERROR_CODE_GET_MONITOR_MAC_FAIL = 2
    const val ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL = 3

    fun upgradeBoundDevice(target: DeviceType, filePath: String,
                           onStart: () -> Unit,
                           onProgressChange: (progress: Int) -> Unit,
                           onSuccess: () -> Unit,
                           onFail: (code: Int, msg: String?) -> Unit) {
        queryTargetDeviceMac(target,
                onSuccess = {
                    enterDfuMode(target,
                            onSuccess = {
                                DeviceManager.scanDelay(object : ScanCallback {
                                    override fun onStart(success: Boolean) {
                                    }

                                    override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                                        val isDeviceVersionValid = isLeScanDeviceVersionValid(target,
                                                device.name ?: "", scanRecord)
                                        if (isDeviceVersionValid) {
                                            if (MacUtil.getLongMacFromStringMac(device.address) - 1 == it) {
                                                startDfuUpgrade(device.address, filePath,
                                                        onStart, onProgressChange, onSuccess, onFail)
                                                DeviceManager.stopScan()
                                            }
                                        }
                                    }

                                    override fun onStop() {
                                    }

                                })
                            },
                            onFail = { code, msg ->
                                onFail(code, msg)
                            }
                    )
                },
                onFail = { code, msg ->
                    onFail(code, msg)
                })
    }

    fun isLeScanDeviceVersionValid(target: DeviceType, name: String, scanRecord: ByteArray): Boolean {
        var isTargetDeviceType = false
        var prefix = when (target) {
            DeviceType.MONITOR -> "M-SUMIAN"
            DeviceType.SLEEP_MASTER -> "PDNWK"
            else -> throw IllegalArgumentException("type $target is illegal")
        }
        isTargetDeviceType = isTargetDeviceType or name.startsWith(prefix)
        return if (isTargetDeviceType) {
            var scanRecordBean = ScanRecord.parseFromBytes(scanRecord)
            return if (scanRecordBean?.serviceUuids != null) {
                scanRecordBean.serviceUuids.toString().contains("fe59")
            } else {
                false
            }
        } else {
            false
        }
    }

    fun queryTargetDeviceMac(target: DeviceType, onSuccess: (mac: Long) -> Unit, onFail: (code: Int, msg: String?) -> Unit) {
        var errorMsg = "未找到设备的物理地址，升级失败，请尝试重连设备"
        when (target) {
            DeviceType.MONITOR -> {
                val monitorMac = DeviceManager.getDevice()?.monitorMac
                if (monitorMac != null) {
                    onSuccess(MacUtil.getLongMacFromStringMac(monitorMac))
                } else {
                    LogManager.deviceUpgradeLog("$errorMsg: target: $target")
                    onFail(ERROR_CODE_GET_MONITOR_MAC_FAIL, errorMsg)
                }
            }
            DeviceType.SLEEP_MASTER -> BleCommunicationController.requestByCmd(
                    BleCmd.QUERY_SLEEP_MASTER_MAC,
                    object : BleRequestCallback {
                        override fun onResponse(bytes: ByteArray, hexString: String) {
                            onSuccess(MacUtil.getLongMacFromCmdBytes(bytes))
                        }

                        override fun onFail(code: Int, msg: String) {
                            LogManager.deviceUpgradeLog("$errorMsg; target: $target code: $code msg: $msg")
                            onFail(ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL, errorMsg)
                        }
                    })
            else -> throw IllegalArgumentException("target must be DeviceType.MONITOR or DeviceType.SLEEP_MASTER")
        }
    }

    fun enterDfuMode(target: DeviceType, onSuccess: () -> Unit, onFail: (code: Int, msg: String?) -> Unit) {
        var errMsg = "进入升级模式失败，请确保设备连接正常并重试"
        val dufCmd =
                if (target == DeviceType.MONITOR) BleCmd.MONITOR_ENTER_DFU else BleCmd.SLEEP_MASTER_ENTER_DFU
        BleCommunicationController.requestByCmd(dufCmd, object : BleRequestCallback {
            override fun onResponse(data: ByteArray, hexString: String) {
                val result = BleCmdUtil.getContentFromData(HexUtil.formatHexString(data))
                if (BleCmd.RESPONSE_CODE_SUCCESS == result) {
                    onSuccess()
                } else {
                    errMsg = getErrorMsg(result)
                    LogManager.deviceUpgradeLog("$errMsg; $target")
                    onFail(ERROR_CODE_ENTER_DFU_MODE_FAIL, errMsg)
                }
            }

            override fun onFail(code: Int, msg: String) {
                LogManager.deviceUpgradeLog("$errMsg; code: $code msg: $msg")
                onFail(code, errMsg)
            }
        }, true)
    }

    fun upgradeDfuDevice(dfuMac: String, path: String,
                         onStart: () -> Unit,
                         onProgressChange: (progress: Int) -> Unit,
                         onSuccess: () -> Unit,
                         onFail: (code: Int, msg: String?) -> Unit) {
        DeviceManager.disconnect()
        startDfuUpgrade(dfuMac, path, onStart, onProgressChange, onSuccess, onFail)
    }

    private fun startDfuUpgrade(dfuMac: String, filePath: String,
                                onStart: () -> Unit,
                                onProgressChange: (progress: Int) -> Unit,
                                onSuccess: () -> Unit,
                                onFail: (code: Int, msg: String?) -> Unit) {
        var context = mApplicationContext!!
        onStart()
        DfuServiceListenerHelper.registerProgressListener(
                context, object : SimpleLogDfuProgressListener {
            override fun onProgressChanged(deviceAddress: String, percent: Int, speed: Float, avgSpeed: Float, currentPart: Int, partsTotal: Int) {
                super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal)
                onProgressChange(percent)
            }

            override fun onDfuAborted(deviceAddress: String) {
                super.onDfuAborted(deviceAddress)
                onFail(ERROR_CODE_DFU_ABORTED, "dfu was aborted")
                DfuServiceListenerHelper.unregisterProgressListener(context, this)
            }

            override fun onDfuCompleted(deviceAddress: String) {
                super.onDfuCompleted(deviceAddress)
                onSuccess()
                DfuServiceListenerHelper.unregisterProgressListener(context, this)
            }

            override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String?) {
                super.onError(deviceAddress, error, errorType, message)
                onFail(error, message)
                DfuServiceListenerHelper.unregisterProgressListener(context, this)
            }
        }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("dfu", "Dfu Service")
        }
        mDfuServiceController = DfuServiceInitiator(dfuMac)
                .setPacketsReceiptNotificationsEnabled(true)
                .setPacketsReceiptNotificationsValue(5)
                .setZip(filePath)
                .setForeground(true)
                .setDisableNotification(true)
                .start(context, DfuServiceImpl::class.java)
    }

    private fun getErrorMsg(errorCode: String?): String {
        return mApplicationContext!!.resources.getString(
                when (errorCode) {
                    "e1" -> R.string.monitor_energy_is_low_please_try_it_later
                    "e2", "e3" -> R.string.monitor_is_syncing_please_try_it_later
                    else -> R.string.error_unknown
                }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service =
                mApplicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    fun reconnectDevice() {
        if (!DeviceManager.isMonitorConnected()) {
            ThreadManager.postToUIThread({ DeviceManager.connectBoundDevice() }, DELAY_RECONNECT_DEVICE)
        }
    }

    fun monitorBatteryLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.monitorBattery < 50
    }

    fun sleepyBatterLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.sleepMasterBattery < 50
    }

    interface QueryTargetMacCallback {
        fun onSuccess(mac: Long)
        fun onFail(code: Int, msg: String?)
    }

    interface EnterDfuCallback {
        fun onSuccess()
        fun onFail(code: Int, msg: String?)
    }

    interface SimpleLogDfuProgressListener : DfuProgressListener {
        override fun onProgressChanged(deviceAddress: String, percent: Int, speed: Float, avgSpeed: Float, currentPart: Int, partsTotal: Int) {
            LogManager.deviceUpgradeLog("dfu onProgressChanged: $deviceAddress $percent $speed $avgSpeed $currentPart $partsTotal")
        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            LogManager.deviceUpgradeLog("dfu onDeviceDisconnecting: $deviceAddress")
        }

        override fun onDeviceDisconnected(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDeviceDisconnected: $deviceAddress")
        }

        override fun onDeviceConnected(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDeviceConnected: $deviceAddress")
        }

        override fun onDfuProcessStarting(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDfuProcessStarting: $deviceAddress")
        }

        override fun onDfuAborted(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDfuAborted: $deviceAddress")
        }

        override fun onEnablingDfuMode(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onEnablingDfuMode: $deviceAddress")
        }

        override fun onDfuCompleted(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDfuCompleted: $deviceAddress")
        }

        override fun onFirmwareValidating(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onFirmwareValidating: $deviceAddress")
        }

        override fun onDfuProcessStarted(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDfuProcessStarted: $deviceAddress")
        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String?) {
            LogManager.deviceUpgradeLog("dfu onError: deviceAddress: $deviceAddress error: $error " +
                    "errorType: $errorType message: $message")
        }

        override fun onDeviceConnecting(deviceAddress: String) {
            LogManager.deviceUpgradeLog("dfu onDeviceConnecting: $deviceAddress")
        }
    }
}

class DfuServiceImpl : DfuBaseService() {

    override fun getNotificationTarget(): Class<out Activity>? {
        return null
    }

    override fun isDebug(): Boolean {
        return true
    }
}