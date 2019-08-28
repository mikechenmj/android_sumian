package com.sumian.sd.buz.upgrade

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.blankj.utilcode.util.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.common.utils.SumianExecutor
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.DeviceType
import com.sumian.device.dfu.UpgradeCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.manager.helper.DfuUpgradeHelper
import com.sumian.device.util.LogManager
import com.sumian.device.util.MacUtil
import com.sumian.sd.app.App
import com.sumian.sd.buz.upgrade.bean.VersionInfo
import com.sumian.sd.buz.version.VersionManager
import java.io.File
import java.lang.IllegalArgumentException

object UpgradeManager {

    private val mContext by lazy { App.getAppContext() }
    private const val DELAY_RECONNECT_DEVICE = 3000L

    private fun getLatestVersionInfo(type: DeviceType): VersionInfo? {
        return when (type) {
            DeviceType.MONITOR -> VersionManager.mFirmwareVersionInfoLD.value?.monitor
            DeviceType.SLEEP_MASTER -> VersionManager.mFirmwareVersionInfoLD.value?.sleeper
            else -> null
        }
    }

    fun queryTargetDeviceMac(target: DeviceType, callback: QueryTargetMacCallback) {
        var errorMsg = "未找到设备的物理地址，升级失败，请尝试重连设备"
        when (target) {
            DeviceType.MONITOR -> {
                val monitorMac = DeviceManager.getDevice()?.monitorMac
                if (monitorMac != null) {
                    callback.onSuccess(MacUtil.getLongMacFromStringMac(monitorMac))
                } else {
                    LogManager.deviceUpgradeLog("$errorMsg: target: $target")
                    callback.onFail(UpgradeCallback.ERROR_CODE_GET_MONITOR_MAC_FAIL, errorMsg)
                }
            }
            DeviceType.SLEEP_MASTER -> BleCommunicationController.requestByCmd(
                    BleCmd.QUERY_SLEEP_MASTER_MAC,
                    object : BleRequestCallback {
                        override fun onResponse(bytes: ByteArray, hexString: String) {
                            callback.onSuccess(MacUtil.getLongMacFromCmdBytes(bytes))
                        }

                        override fun onFail(code: Int, msg: String) {
                            LogManager.deviceUpgradeLog("$errorMsg; target: $target code: $code msg: $msg")
                            callback.onFail(UpgradeCallback.ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL, errorMsg)
                        }
                    })
            else -> throw IllegalArgumentException("target must be DeviceType.MONITOR or DeviceType.SLEEP_MASTER")
        }
    }

    fun upgradeDfuDevice(upgradeCallback: UpgradeCallback, dfuMac: String, type: DeviceType) {
        downloadUpgradeFile(object : DownloadCallback {
            override fun onCompleted(path: String) {
                DeviceManager.disconnect()
                DfuUpgradeHelper.startDfuUpgrade(dfuMac, path, upgradeCallback)
            }

            override fun onError(e: Throwable?) {
                upgradeCallback.onFail(UpgradeCallback.ERROR_CODE_DOWNLOAD_FILE_FAIL, e?.message)
            }

            override fun onProgress(soFarBytes: Int, totalBytes: Int) {
            }

            override fun onPaused(soFarBytes: Int, totalBytes: Int) {
            }
        }, type)
    }

    fun downloadUpgradeFile(downloadCallback: DownloadCallback, type: DeviceType): File? {
        val latestVersionInfo = getLatestVersionInfo(type) ?: return null
        val dir = mContext.getDir("upgrade", 0)
        val file = File(dir, latestVersionInfo.version + "zip")
        FileDownloader.setup(mContext)
        FileDownloader.getImpl().create(latestVersionInfo.url)
                .setPath(file.path)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask?) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        val fileMD5 = FileUtils.getFileMD5ToString(file)
                        if (!fileMD5.equals(latestVersionInfo.md5, true)) {
                            downloadCallback.onError(Throwable("文件损坏，请重新下载"))
                            return
                        }
                        downloadCallback.onCompleted(file.absolutePath)
                    }

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        downloadCallback.onError(e)
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        downloadCallback.onProgress(soFarBytes, totalBytes)
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        downloadCallback.onPaused(soFarBytes, totalBytes)
                    }
                })
                .start()
        return file
    }

    fun mobileBatteryLow(): Boolean {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = mContext.registerReceiver(null, iFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level / scale.toFloat()
        val batteryQuantity = (batteryPct * 100).toInt()
        val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        // 电量小于50，而且不在充电状态
        return batteryQuantity < 50 && !isCharging
    }

    fun monitorBatteryLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.monitorBattery < 50
    }

    fun sleepyBatterLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.sleepMasterBattery < 50
    }

//    private fun upgradeSuccess() {
//        VersionManager.queryDeviceVersion()
//        LogManager.deviceUpgradeLog("设备dfu固件升级完成")
//        reconnectDevice()
//    }

    fun reconnectDevice() {
        if (!DeviceManager.isMonitorConnected()) {
            SumianExecutor.runOnUiThread({ DeviceManager.connectBoundDevice() }, DELAY_RECONNECT_DEVICE)
        }
    }

//    private fun upgradeBoundDevice(type: DeviceType, dfuCallback: UpgradeCallback) {
//        var file = mUpgradeFile
//        if (!checkBattery(type)) {
//            return
//        }
//        if (file == null) {
//            return
//        }
//        DeviceManager.upgradeBoundDevice(
//                type,
//                file.absolutePath,
//                object : UpgradeCallback {
//                    override fun onStart() {
//                        dfuCallback.onStart()
//                    }
//
//                    override fun onProgressChange(progress: Int) {
//                        dfuCallback.onProgressChange(progress)
//                    }
//
//                    override fun onSuccess() {
//                        dfuCallback.onSuccess()
//                        upgradeSuccess()
//                    }
//
//                    override fun onFail(code: Int, msg: String?) {
//                        dfuCallback.onFail(code, msg)
//                        reconnectDevice()
//                    }
//                })
//    }
//
//    private fun checkBattery(type: DeviceType): Boolean {
//        if (this.mobileBatteryLow()) {
//            LogManager.deviceUpgradeLog("手机电量不足50%,无法进行 dfu 升级")
//            return false
//        }
//        if (!DeviceManager.isMonitorConnected()) {
//            ToastUtils.showShort(R.string.device_not_connected)
//            return false
//        }
//        when (type) {
//            DeviceType.MONITOR -> if (monitorBatteryLow()) {
//                LogManager.deviceUpgradeLog("监测仪电量不足50%,无法进行 dfu 升级")
//                return false
//            }
//            DeviceType.SLEEP_MASTER -> {
//                if (!DeviceManager.isSleepMasterConnected()) {
//                    ToastUtils.showShort(R.string.device_not_connected)
//                    return false
//                }
//                if (sleepyBatterLow()) {
//                    LogManager.deviceUpgradeLog("速眠仪电量不足50%,无法进行 dfu 升级")
//                    return false
//                }
//            }
//        }
//        return true
//    }
//
//    private fun mobileBatteryLow(): Boolean {
//        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
//        val batteryStatus = mContext.registerReceiver(null, iFilter)
//        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
//        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
//        val batteryPct = level / scale.toFloat()
//        val batteryQuantity = (batteryPct * 100).toInt()
//        val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
//        // 电量小于50，而且不在充电状态
//        return batteryQuantity < 50 && !isCharging
//    }
//
//    private fun monitorBatteryLow(): Boolean {
//        val device = DeviceManager.getDevice()
//        return device != null && device.monitorBattery < 50
//    }
//
//    private fun sleepyBatterLow(): Boolean {
//        val device = DeviceManager.getDevice()
//        return device != null && device.sleepMasterBattery < 50
//    }

    interface QueryTargetMacCallback {
        fun onSuccess(mac: Long)
        fun onFail(code: Int, msg: String?)
    }

    interface DownloadCallback {
        fun onCompleted(path: String)
        fun onError(e: Throwable?)
        fun onProgress(soFarBytes: Int, totalBytes: Int)
        fun onPaused(soFarBytes: Int, totalBytes: Int)
    }
}