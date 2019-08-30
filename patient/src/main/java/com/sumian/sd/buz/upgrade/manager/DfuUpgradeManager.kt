package com.sumian.sd.buz.upgrade.manager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.blankj.utilcode.util.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.device.data.DeviceType
import com.sumian.device.dfu.DfuUpgradeHelper
import com.sumian.sd.app.App
import com.sumian.sd.buz.upgrade.bean.VersionInfo
import com.sumian.sd.buz.version.VersionManager
import java.io.File
import java.lang.IllegalArgumentException

object DfuUpgradeManager {

    const val TYPE_MONITOR = 0
    const val TYPE_SLEEP_MASTER = 1
    const val TYPE_ALL = 2

    private val mContext: Context by lazy { App.getAppContext() }

    private fun getLatestVersionInfo(type: Int): VersionInfo? {
        return when (type) {
            TYPE_MONITOR -> VersionManager.mFirmwareVersionInfoLD.value?.monitor
            TYPE_SLEEP_MASTER -> VersionManager.mFirmwareVersionInfoLD.value?.sleeper
            else -> null
        }
    }

    private fun getTypeFromInt(type: Int): DeviceType {
        return when (type) {
            TYPE_MONITOR -> DeviceType.MONITOR
            TYPE_SLEEP_MASTER -> DeviceType.SLEEP_MASTER
            else -> throw IllegalArgumentException("type: $type is illegal")
        }
    }

    fun downloadUpgradeFile(downloadCallback: DownloadCallback, type: Int): File? {
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

    fun queryTargetDeviceMac(type: Int, onSuccess: (mac: Long) -> Unit, onFail: (code: Int, msg: String?) -> Unit) {
        DfuUpgradeHelper.queryTargetDeviceMac(getTypeFromInt(type), onSuccess, onFail)
    }

    fun enterDfuMode(type: Int, onSuccess: () -> Unit, onFail: (code: Int, msg: String?) -> Unit) {
        DfuUpgradeHelper.enterDfuMode(getTypeFromInt(type), onSuccess, onFail)
    }

    fun upgradeDfuDevice(dfuMac: String, path: String,
                         onStart: () -> Unit,
                         onProgressChange: (progress: Int) -> Unit,
                         onSuccess: () -> Unit,
                         onFail: (code: Int, msg: String?) -> Unit) {
        DfuUpgradeHelper.upgradeDfuDevice(dfuMac, path, onStart, onProgressChange, onSuccess, onFail)
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
        return DfuUpgradeHelper.monitorBatteryLow()
    }

    fun sleepyBatterLow(): Boolean {
        return DfuUpgradeHelper.sleepyBatterLow()
    }

    fun reconnectDevice() {
        DfuUpgradeHelper.reconnectDevice()
    }

    interface DownloadCallback {
        fun onCompleted(path: String)
        fun onError(e: Throwable?)
        fun onProgress(soFarBytes: Int, totalBytes: Int)
        fun onPaused(soFarBytes: Int, totalBytes: Int)
    }

    interface UpgradeCallback {

        companion object {
            const val ERROR_CODE_DOWNLOAD_FILE_FAIL = 4
        }

        fun onStart()
        fun onProgressChange(progress: Int)
        fun onSuccess()
        fun onFail(code: Int, msg: String?)
    }
}