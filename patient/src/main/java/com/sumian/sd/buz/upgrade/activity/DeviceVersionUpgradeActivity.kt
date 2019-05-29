package com.sumian.sd.buz.upgrade.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.SumianExecutor
import com.sumian.common.widget.TitleBar
import com.sumian.device.data.DeviceType
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.DfuCallback
import com.sumian.sd.R
import com.sumian.sd.buz.setting.version.VersionManager
import com.sumian.sd.buz.upgrade.bean.VersionInfo
import com.sumian.sd.buz.upgrade.dialog.VersionDialog
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.hw_activity_main_version_upgrade.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*

/**
 * Created by jzz
 * on 2017/10/27.
 *
 *
 * desc:固件升级提醒模块
 */

class DeviceVersionUpgradeActivity : BaseViewModelActivity<BaseViewModel>(), TitleBar.OnBackClickListener, EasyPermissions.PermissionCallbacks {
    private var mType: Int = 0
    private var mIsLatestVersion: Boolean = false
    private var mUpgradeFile: File? = null

    companion object {
        const val TYPE_MONITOR = 0
        const val TYPE_SLEEP_MASTER = 1
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_VERSION_IS_LATEST = "extra_version_latest"
        private const val UPGRADE_RECONNECT_WAIT_DURATION = (1000 * 45).toLong()
        private const val REQUEST_WRITE_PERMISSION = 1000

        fun show(context: Context, versionType: Int, haveLatestVersion: Boolean) {
            val intent = Intent(context, DeviceVersionUpgradeActivity::class.java)
            intent.putExtra(EXTRA_TYPE, versionType)
            intent.putExtra(EXTRA_VERSION_IS_LATEST, haveLatestVersion)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.hw_activity_main_version_upgrade
    }

    override fun initBundle(bundle: Bundle) {
        mType = bundle.getInt(EXTRA_TYPE)
        mIsLatestVersion = bundle.getBoolean(EXTRA_VERSION_IS_LATEST)
    }

    override fun initWidget() {
        super.initWidget()
        val titleBar = findViewById<TitleBar>(R.id.title_bar)
        titleBar.setOnBackClickListener(this)
        titleBar.setTitle(if (mType == TYPE_MONITOR) "监测仪升级" else "速眠仪升级")
        bt_download.setOnClickListener { downloadUpgradeFileWithPermissionCheck() }
        bt_upgrade.setOnClickListener { upgrade(mUpgradeFile) }
    }

    override fun initData() {
        super.initData()
        val newVersion: String? = getLatestVersionInfo()?.version
        val currentVersion: String? = getCurrentVersion()
        iv_upgrade!!.setImageResource(if (mIsLatestVersion) R.mipmap.set_icon_download else R.mipmap.set_icon_success)
        tv_version_latest!!.text = if (mIsLatestVersion) String.format(Locale.getDefault(), getString(R.string.latest_version), newVersion) else getString(R.string.firmware_note_hint)
        tv_version_current!!.text = String.format(Locale.getDefault(), getString(R.string.current_version_hint), currentVersion)
        bt_download!!.visibility = if (mIsLatestVersion) View.VISIBLE else View.GONE
    }

    private fun getCurrentVersion(): String? {
        return when (mType) {
            TYPE_MONITOR -> {
                DeviceManager.getMonitorSoftwareVersion()
            }
            else -> {
                DeviceManager.getSleepMasterSoftwareVersion()
            }
        }
    }

    private fun getLatestVersionInfo(): VersionInfo? {
        return if (mType == TYPE_MONITOR) {
            VersionManager.mFirmwareVersionInfoLD.value!!.monitor
        } else {
            VersionManager.mFirmwareVersionInfoLD.value!!.sleeper
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage_permission, Toast.LENGTH_SHORT).show()
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_WRITE_PERMISSION)
    private fun downloadUpgradeFileWithPermissionCheck() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            downloadUpgradeFile()
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启文件读写权限.", REQUEST_WRITE_PERMISSION, *perms)
        }
    }

    private fun checkBattery(): Boolean {
        if (this.mobileBatteryLow()) {
            LogManager.appendPhoneLog("手机电量不足50%,无法进行 dfu 升级")
            showErrorDialog(R.string.phone_bettery_low_title, R.string.phone_bettery_low_message)
            return false
        }
        if (!DeviceManager.isMonitorConnected()) {
            ToastUtils.showShort(R.string.device_not_connected)
            return false
        }
        when (mType) {
            TYPE_MONITOR -> if (monitorBatteryLow()) {
                LogManager.appendMonitorLog("监测仪电量不足50%,无法进行 dfu 升级")
                showErrorDialog(R.string.monitor_bettery_low_title, R.string.monitor_bettery_low_message)
                return false
            }
            TYPE_SLEEP_MASTER -> {
                if (!DeviceManager.isSleepMasterConnected()) {
                    ToastUtils.showShort(R.string.device_not_connected)
                    return false
                }
                if (sleepyBatterLow()) {
                    LogManager.appendSpeedSleeperLog("速眠仪电量不足50%,无法进行 dfu 升级")
                    showErrorDialog(R.string.sleeper_bettery_low_title, R.string.sleeper_bettery_low_message)
                    return false
                }
            }
        }
        return true
    }

    private fun showErrorDialog(title: Int, message: Int) {
        SumianAlertDialog(this)
                .hideTopIcon(true)
                .setTitle(title)
                .setMessage(message)
                .setRightBtn(R.string.confirm, null)
                .show()
    }

    override fun onBack(v: View) {
        finish()
    }

    private fun mobileBatteryLow(): Boolean {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = this.registerReceiver(null, iFilter)
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

    private fun downloadUpgradeFile() {
        val latestVersionInfo = getLatestVersionInfo()!!
        val dir = getDir("upgrade", 0)
        val file = File(dir, latestVersionInfo.version + "zip")
        val progressDialog = VersionDialog.newInstance(getString(R.string.firmware_download_title_hint))
        progressDialog.show(supportFragmentManager, progressDialog.javaClass.simpleName)
        mUpgradeFile = file
        FileDownloader.setup(this)
        FileDownloader.getImpl().create(latestVersionInfo.url)
                .setPath(file.path)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask?) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        progressDialog.dismiss()
                        val fileMD5 = FileUtils.getFileMD5ToString(file)
                        if (!fileMD5.equals(latestVersionInfo.md5, true)) {
                            ToastUtils.showShort("文件损坏，请重新下载")
                            return
                        }
                        iv_upgrade!!.setImageResource(R.mipmap.set_icon_upgrade)
                        ToastHelper.show(R.string.firmware_download_success_hint)
                        bt_download.isVisible = false
                        bt_upgrade.isVisible = true
                        upgrade(mUpgradeFile)
                    }

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        progressDialog.dismiss()
                        ToastUtils.showShort(e?.message)
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        progressDialog.updateProgress(soFarBytes * 100 / totalBytes)
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                })
                .start()

    }

    private fun upgrade(file: File?) {
        if (!checkBattery()) {
            return
        }
        if (file == null) {
            return
        }
        val progressDialog = VersionDialog.newInstance(getString(R.string.firmware_upgrade_title_hint))
        progressDialog.show(supportFragmentManager, progressDialog.javaClass.simpleName)
        DeviceManager.upgrade(
                if (mType == TYPE_MONITOR) DeviceType.MONITOR else DeviceType.SLEEP_MASTER,
                file.absolutePath,
                object : DfuCallback {
                    override fun onStart() {
                    }

                    override fun onProgressChange(progress: Int) {
                        progressDialog.updateProgress(progress)
                    }

                    override fun onSuccess() {
                        progressDialog.dismiss()
                        onUpgradeSuccess()
                    }

                    override fun onFail(code: Int, msg: String?) {
                        ToastUtils.showShort("升级失败：$msg")
                        progressDialog.dismiss()
                    }
                })
    }

    private fun onUpgradeSuccess() {
        ToastUtils.showLong(when (mType) {
            TYPE_MONITOR -> R.string.firmware_upgrade_success_hint
            else -> R.string.sleeper_firmware_upgrade_success_hint
        })
        VersionManager.getAndCheckFirmVersionShowUpgradeDialogIfNeed(false)
        LogManager.appendMonitorLog("设备dfu固件升级完成")
        SumianExecutor.runOnUiThread({ DeviceManager.connectBoundDevice() }, UPGRADE_RECONNECT_WAIT_DURATION);
        finish()
    }

}