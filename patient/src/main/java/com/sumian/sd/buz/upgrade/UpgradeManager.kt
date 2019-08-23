package com.sumian.sd.buz.upgrade

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.common.utils.SumianExecutor
import com.sumian.device.data.DeviceType
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.DfuCallback
import com.sumian.device.manager.helper.UpgradeDeviceHelper
import com.sumian.sd.R
import com.sumian.sd.buz.upgrade.bean.VersionInfo
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.log.LogManager
import java.io.File

class UpgradeManager(var mContext: Context, var mType: DeviceType) {

    private var mUpgradeFile: File? = null

    companion object {
        const val DELAY_RECONNECT_DEVICE = 3000L
    }

    private fun getLatestVersionInfo(): VersionInfo? {
        return when (mType) {
            DeviceType.MONITOR -> VersionManager.mFirmwareVersionInfoLD.value?.monitor
            DeviceType.SLEEP_MASTER -> VersionManager.mFirmwareVersionInfoLD.value?.sleeper
            else -> null
        }
    }

    fun upgradeBoundDevice(downloadCallback: DownloadCallback, dfuCallback: DfuCallback) {
        upgradeAfterDownload(downloadCallback) { upgradeBoundDevice(dfuCallback) }
    }

    fun upgradeDfuModelDevice(downloadCallback: DownloadCallback, dfuCallback: DfuCallback, dfuMac: String) {
        upgradeAfterDownload(downloadCallback) { filePath ->
            DeviceManager.disconnect()
            UpgradeDeviceHelper.upgradeDfuModelDevice(dfuMac, filePath, dfuCallback)
        }
    }

    private fun upgradeAfterDownload(downloadCallback: DownloadCallback, upgrade: (filePath: String) -> Unit): File? {
        val latestVersionInfo = getLatestVersionInfo() ?: return null
        val dir = mContext.getDir("upgrade", 0)
        val file = File(dir, latestVersionInfo.version + "zip")
        mUpgradeFile = file
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
                        upgrade(file.absolutePath)
                        downloadCallback.onCompleted()
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

    fun upgradeBoundDevice(target: DeviceType, filePath: String, callback: DfuCallback) {
        UpgradeDeviceHelper.upgradeBoundDevice(DeviceManager.mApplication, target, filePath, callback)
    }

    private fun upgradeBoundDevice(dfuCallback: DfuCallback) {
        var file = mUpgradeFile
        if (!checkBattery()) {
            return
        }
        if (file == null) {
            return
        }
        DeviceManager.upgradeBoundDevice(
                mType,
                file.absolutePath,
                object : DfuCallback {
                    override fun onStart() {
                        dfuCallback.onStart()
                    }

                    override fun onProgressChange(progress: Int) {
                        dfuCallback.onProgressChange(progress)
                    }

                    override fun onSuccess() {
                        dfuCallback.onSuccess()
                        upgradeSuccess()
                    }

                    override fun onFail(code: Int, msg: String?) {
                        dfuCallback.onFail(code, msg)
                        reconnectDevice()
                    }
                })
    }

    private fun checkBattery(): Boolean {
        if (this.mobileBatteryLow()) {
            LogManager.appendPhoneLog("手机电量不足50%,无法进行 dfu 升级")
            return false
        }
        if (!DeviceManager.isMonitorConnected()) {
            ToastUtils.showShort(R.string.device_not_connected)
            return false
        }
        when (mType) {
            DeviceType.MONITOR -> if (monitorBatteryLow()) {
                LogManager.appendMonitorLog("监测仪电量不足50%,无法进行 dfu 升级")
                return false
            }
            DeviceType.SLEEP_MASTER -> {
                if (!DeviceManager.isSleepMasterConnected()) {
                    ToastUtils.showShort(R.string.device_not_connected)
                    return false
                }
                if (sleepyBatterLow()) {
                    LogManager.appendSpeedSleeperLog("速眠仪电量不足50%,无法进行 dfu 升级")
                    return false
                }
            }
        }
        return true
    }

    private fun mobileBatteryLow(): Boolean {
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

    private fun monitorBatteryLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.monitorBattery < 50
    }

    private fun sleepyBatterLow(): Boolean {
        val device = DeviceManager.getDevice()
        return device != null && device.sleepMasterBattery < 50
    }

    private fun upgradeSuccess() {
        VersionManager.queryDeviceVersion()
        LogManager.appendMonitorLog("设备dfu固件升级完成")
        reconnectDevice()
    }

    fun reconnectDevice() {
        if (!DeviceManager.isMonitorConnected()) {
            SumianExecutor.runOnUiThread({ DeviceManager.connectBoundDevice() }, DELAY_RECONNECT_DEVICE)
        }
    }

    interface DownloadCallback {
        fun onCompleted()
        fun onError(e: Throwable?)
        fun onProgress(soFarBytes: Int, totalBytes: Int)
        fun onPaused(soFarBytes: Int, totalBytes: Int)
    }
}