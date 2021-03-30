package com.sumian.sd.buz.upgrade.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.TitleBar
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.device.scan.ScanDeviceActivity
import com.sumian.sd.buz.upgrade.bean.VersionInfo
import com.sumian.sd.buz.upgrade.dialog.VersionDialog
import com.sumian.sd.buz.upgrade.manager.DfuUpgradeManager
import com.sumian.sd.buz.upgrade.manager.DfuUpgradeManager.TYPE_MONITOR
import com.sumian.sd.buz.upgrade.manager.DfuUpgradeManager.TYPE_SLEEP_MASTER
import com.sumian.sd.buz.version.VersionManager
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
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_VERSION_IS_LATEST = "extra_version_latest"
        private const val REQUEST_UPGRADE_PERMISSION = 1000

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
        bt_upgrade.setOnClickListener { enterDfuAndUpgrade(mUpgradeFile) }
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
            VersionManager.mFirmwareVersionInfoLD.value?.monitor
        } else {
            VersionManager.mFirmwareVersionInfoLD.value?.sleeper
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, R.string.device_version_upgrade_permission_fail, Toast.LENGTH_SHORT).show()
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_UPGRADE_PERMISSION)
    private fun downloadUpgradeFileWithPermissionCheck() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val progressDialog = VersionDialog.newInstance(getString(R.string.firmware_download_title_hint))

            mUpgradeFile = DfuUpgradeManager.downloadUpgradeFile(object : DfuUpgradeManager.DownloadCallback {
                override fun started(task: BaseDownloadTask?) {
                    progressDialog.show(supportFragmentManager, progressDialog.javaClass.simpleName)
                }

                override fun onCompleted(path: String) {
                    if (progressDialog.isResumed) {
                        progressDialog.dismiss()
                    }
                    iv_upgrade!!.setImageResource(R.mipmap.set_icon_upgrade)
                    ToastHelper.show(R.string.firmware_download_success_hint)
                    bt_download.isVisible = false
                    bt_upgrade.isVisible = true
                }

                override fun onError(e: Throwable?) {
                    if (progressDialog.isResumed) {
                        progressDialog.dismiss()
                    }
                    ToastUtils.showShort(e?.message)
                }

                override fun onProgress(soFarBytes: Int, totalBytes: Int) {
                    progressDialog.updateProgress(soFarBytes * 100 / totalBytes)
                }

                override fun onPaused(soFarBytes: Int, totalBytes: Int) {
                }
            }, mType)
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启位置以及文件读写权限.", REQUEST_UPGRADE_PERMISSION, *perms)
        }
    }

    private fun checkBattery(): Boolean {
        if (DfuUpgradeManager.mobileBatteryLow()) {
            LogManager.appendPhoneLog("手机电量不足50%,无法进行 dfu 升级")
            showErrorDialog(R.string.phone_bettery_low_title, R.string.phone_bettery_low_message)
            return false
        }
        if (!DeviceManager.isMonitorConnected()) {
            ToastUtils.showShort(R.string.device_not_connected)
            return false
        }
        when (mType) {
            TYPE_MONITOR -> if (DfuUpgradeManager.monitorBatteryLow()) {
                LogManager.appendMonitorLog("监测仪电量不足50%,无法进行 dfu 升级")
                showErrorDialog(R.string.monitor_bettery_low_title, R.string.monitor_bettery_low_message)
                return false
            }
            TYPE_SLEEP_MASTER -> {
                if (!DeviceManager.isSleepMasterConnected()) {
                    ToastUtils.showShort(R.string.device_not_connected)
                    return false
                }
                if (DfuUpgradeManager.sleepyBatterLow()) {
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

    private fun enterDfuAndUpgrade(file: File?) {
        if (!checkBattery()) {
            return
        }
        if (file == null) {
            return
        }
        var type = mType
        DfuUpgradeManager.queryTargetDeviceMac(
                type,
                onSuccess = {
                    DfuUpgradeManager.enterDfuMode(
                            type,
                            onSuccess = {
                                ScanDeviceActivity.startForUpgrade(this@DeviceVersionUpgradeActivity, type)
                                finish()
                            },
                            onFail = { _, msg ->
                                ToastUtils.showLong(msg)
                            })
                },
                onFail = { _, msg ->
                    ToastUtils.showLong(msg)
                })
    }
}