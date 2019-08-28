package com.sumian.device.manager.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.clj.fastble.utils.HexUtil
import com.sumian.device.R
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.callback.ScanCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.DeviceType
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.manager.helper.DfuCallback.Companion.ERROR_CODE_ENTER_DFU_MODE_FAIL
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_UNKNOWN
import com.sumian.device.util.BleCmdUtil
import com.sumian.device.util.LogManager
import com.sumian.device.util.MacUtil
import no.nordicsemi.android.dfu.*
import java.lang.ref.WeakReference

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/9 10:26
 * desc   :
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object UpgradeDeviceHelper {
    private var mDfuServiceController: DfuServiceController? = null
    private var mApplicationContext: Context? = DeviceManager.mApplication
    private var mDfuCallbackWR: WeakReference<DfuCallback>? = null
    private var mUpgradeBoundDfuCallback: DfuCallback? = null
    private var mUpgradeDfuModeCallback: DfuCallback? = null

    private const val UPGRADE_RECONNECT_WAIT_DURATION = 3000L

    fun upgradeBoundDevice(context: Context, target: DeviceType, filePath: String, callback: DfuCallback, onDfuCmdSuccess: (() -> Unit)? = null) {
        mDfuCallbackWR = WeakReference(callback)
        mUpgradeBoundDfuCallback = callback
        mUpgradeBoundDfuCallback?.onStart()
        if (target == DeviceType.MONITOR) {
            val monitorMac = DeviceManager.getDevice()?.monitorMac
            if (monitorMac == null) {
                mUpgradeBoundDfuCallback?.onFail(DfuCallback.ERROR_CODE_UNKNOWN, "monitor mac is null")
                return
            }
            val longMac = MacUtil.getLongMacFromStringMac(monitorMac)
            enterDfuModeAndDfu(target, filePath, longMac + 1, onDfuCmdSuccess)
        } else {
            BleCommunicationController.requestByCmd(
                    BleCmd.QUERY_SLEEP_MASTER_MAC,
                    object : BleRequestCallback {
                        override fun onResponse(bytes: ByteArray, hexString: String) {
                            val longMac = MacUtil.getLongMacFromCmdBytes(bytes)
                            enterDfuModeAndDfu(target, filePath, longMac + 1, onDfuCmdSuccess)
                        }

                        override fun onFail(code: Int, msg: String) {
                            mUpgradeBoundDfuCallback?.onFail(DfuCallback.ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL, msg)
                        }
                    })
        }
    }

    private fun enterDfuModeAndDfu(target: DeviceType, filePath: String, longDfuMac: Long, onDfuCmdSuccess: (() -> Unit)? = null) {
        val dufCmd =
                if (target == DeviceType.MONITOR) BleCmd.MONITOR_ENTER_DFU else BleCmd.SLEEP_MASTER_ENTER_DFU
        BleCommunicationController.requestByCmd(dufCmd, object : BleRequestCallback {
            override fun onResponse(data: ByteArray, hexString: String) {
                val result = BleCmdUtil.getContentFromData(HexUtil.formatHexString(data))
                if (BleCmd.RESPONSE_CODE_SUCCESS == result) {
                    if (onDfuCmdSuccess != null) {
                        onDfuCmdSuccess()
                    } else {
                        scanAndDfuUpgrade(longDfuMac, filePath)
                    }
                } else {
                    mUpgradeBoundDfuCallback?.onFail(ERROR_CODE_ENTER_DFU_MODE_FAIL, getErrorMsg(result))
                }
            }

            override fun onFail(code: Int, msg: String) {
                mUpgradeBoundDfuCallback?.onFail(code, msg)
            }
        })
    }

    private fun scanAndDfuUpgrade(longDfuMac: Long, filePath: String) {
        val dfuMac = MacUtil.getStringMacFromLong(longDfuMac)
        DeviceManager.disconnect()
        DeviceManager.scanDelay(object : ScanCallback {
            private var mDeviceFound = false

            override fun onStart(success: Boolean) {
            }

            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                if (device.address.toUpperCase() == dfuMac.toUpperCase()) {
                    mDeviceFound = true
                    DeviceManager.stopScan()
                    upgradeDfuModelDevice(dfuMac, filePath)
                }
            }

            override fun onStop() {
                if (!mDeviceFound) {
                    mUpgradeBoundDfuCallback
                            ?.onFail(ERROR_CODE_UNKNOWN, "connect device failed")
                }
            }
        })
    }

    fun upgradeDfuModelDevice(dfuMac: String, filePath: String, dfuCallback: DfuCallback? = null) {
        DfuServiceListenerHelper.registerProgressListener(
                mApplicationContext!!,
                mDfuProgressListener
        )
        mUpgradeDfuModeCallback = dfuCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("dfu", "Dfu Service")
        }
        mDfuServiceController = DfuServiceInitiator(dfuMac)
                .setPacketsReceiptNotificationsEnabled(true)
                .setPacketsReceiptNotificationsValue(5)
                .setZip(filePath)
                .setDisableNotification(false)
                .setForeground(true)
                .setDisableNotification(true)
                .start(mApplicationContext!!, DfuServiceImpl::class.java)
        mUpgradeDfuModeCallback?.onStart()
    }

    fun pauseDfu() {
        mDfuServiceController?.pause()
    }

    private val mDfuProgressListener = object : DfuProgressListener {
        override fun onProgressChanged(
                deviceAddress: String,
                percent: Int,
                speed: Float,
                avgSpeed: Float,
                currentPart: Int,
                partsTotal: Int
        ) {
            mUpgradeBoundDfuCallback?.onProgressChange(percent)
            mUpgradeDfuModeCallback?.onProgressChange(percent)
        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            LogManager.log("dfu onDeviceDisconnecting")
        }

        override fun onDeviceDisconnected(deviceAddress: String) {
            LogManager.log("dfu onDeviceDisconnected")
        }

        override fun onDeviceConnected(deviceAddress: String) {
            LogManager.log("dfu onDeviceConnected")
        }

        override fun onDfuProcessStarting(deviceAddress: String) {
            LogManager.log("dfu onDfuProcessStarting")
        }

        override fun onDfuAborted(deviceAddress: String) {
            LogManager.log("dfu onDfuAborted")
        }

        override fun onEnablingDfuMode(deviceAddress: String) {
            LogManager.log("dfu onEnablingDfuMode")
        }

        override fun onDfuCompleted(deviceAddress: String) {
            LogManager.log("dfu onDfuCompleted")
            mUpgradeBoundDfuCallback?.onSuccess()
            mUpgradeDfuModeCallback?.onSuccess()
            mUpgradeBoundDfuCallback = null
            mUpgradeDfuModeCallback = null
        }

        override fun onFirmwareValidating(deviceAddress: String) {
            LogManager.log("dfu onFirmwareValidating")
        }

        override fun onDfuProcessStarted(deviceAddress: String) {
            LogManager.log("dfu onDfuProcessStarted")
        }

        override fun onError(deviceAddress: String, error: Int, errorType: Int, message: String?) {
            LogManager.log("dfu onError: $message")
            mUpgradeBoundDfuCallback?.onFail(error, message)
            mUpgradeDfuModeCallback?.onFail(error, message)
            mUpgradeBoundDfuCallback = null
            mUpgradeDfuModeCallback = null
        }

        override fun onDeviceConnecting(deviceAddress: String) {
            LogManager.log("dfu onDeviceConnecting")
        }

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
            Handler(Looper.getMainLooper()).postDelayed({ DeviceManager.connectBoundDevice() }, UPGRADE_RECONNECT_WAIT_DURATION)
        }
    }

}

interface DfuCallback {
    companion object {

        const val ERROR_CODE_UNKNOWN = 0
        const val ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL = 1
        const val ERROR_CODE_ENTER_DFU_MODE_FAIL = 2

    }

    fun onStart()
    fun onProgressChange(progress: Int)
    fun onSuccess()
    fun onFail(code: Int, msg: String?)
}

class DfuServiceImpl : DfuBaseService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getNotificationTarget(): Class<out Activity>? {
        return null
    }

    override fun isDebug(): Boolean {
        return true
    }
}