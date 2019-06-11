package com.sumian.sd.buz.setting.version

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.device.widget.UpgradeFirmwareDialogActivity
import com.sumian.sd.buz.setting.version.bean.Version
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.network.response.FirmwareVersionInfo
import com.sumian.sd.common.utils.UiUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/2/28 14:00
 * desc   :
 * version: 1.0
 */
object VersionManager {
    const val UPGRADE_MODE_NO_UPGRADE = -1
    const val UPGRADE_MODE_NORMAL = 0
    const val UPGRADE_MODE_FORCE = 1
    const val UPGRADE_MODE_MUTE = 2

    private const val VERSION_TYPE_MONITOR = 0
    private const val VERSION_TYPE_SLEEPER = 1

    val mFirmwareVersionInfoLD = MutableLiveData<FirmwareVersionInfo>()

    val mAppUpgradeMode by lazy {
        val liveData = MutableLiveData<Int>()
        liveData.value = UPGRADE_MODE_NO_UPGRADE
        liveData
    }

    init {
        queryAppVersion()
    }

    fun queryAppVersion() {
        var currentVersion = UiUtils.getPackageInfo(App.getAppContext()).versionName
        if (currentVersion.indexOf("-") != -1) {
            currentVersion = currentVersion.subSequence(0, currentVersion.indexOf("-")).toString()
        }
        val call = AppManager.getSdHttpService().getAppVersion(currentVersion = currentVersion)
        call.enqueue(object : BaseSdResponseCallback<Version>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse.message)
            }

            override fun onSuccess(response: Version?) {
                response?.let { it ->
                    it.version?.let {
                        val currentVersionCodes = currentVersion.split(".")
                        val onlineVersionCodes = it.split(".")
                        val isHaveUpgrade = VersionUtil.hasNewVersion(onlineVersionCodes, currentVersionCodes)
                        if (isHaveUpgrade) {
                            mAppUpgradeMode.value = response.show_update_mode
                        } else {
                            mAppUpgradeMode.value = UPGRADE_MODE_NO_UPGRADE
                        }
                    }
                }
            }
        })
    }

    fun updateFirmVersion() {
        getAndCheckFirmVersionShowUpgradeDialogIfNeed(false)
    }

    fun getAndCheckFirmVersionShowUpgradeDialogIfNeed(showDialogIfNeed: Boolean) {
        val device = DeviceManager.getDevice()
        val call = AppManager.getSdHttpService().getFirmwareLatestVersion(
                device?.monitorVersionInfo?.hardwareVersion,
                device?.sleepMasterVersionInfo?.hardwareVersion)
        call.enqueue(object : BaseSdResponseCallback<FirmwareVersionInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {}

            override fun onSuccess(response: FirmwareVersionInfo?) {
                if (response == null) return
                mFirmwareVersionInfoLD.value = response
                if (showDialogIfNeed) {
                    if (hasNewMonitorVersion() || hasNewSleeperVersion()) {
                        UpgradeFirmwareDialogActivity.start(if (hasNewMonitorVersion()) VERSION_TYPE_MONITOR else VERSION_TYPE_SLEEPER, false)
                    }
                }
            }

            override fun onFinish() {}
        })
    }

    private fun hasNewVersion(versionType: Int, firmwareVersionInfo: FirmwareVersionInfo?): Boolean {
        if (firmwareVersionInfo == null) return false
        val isConnected = if (versionType == VERSION_TYPE_MONITOR) DeviceManager.isMonitorConnected() else DeviceManager.isSleepMasterConnected()
        val currentVersionInfo = if (versionType == VERSION_TYPE_MONITOR) DeviceManager.getMonitorSoftwareVersion() else DeviceManager.getSleepMasterSoftwareVersion()
        val newVersionInfo = if (versionType == VERSION_TYPE_MONITOR) firmwareVersionInfo.monitor else firmwareVersionInfo.sleeper
        return (isConnected
                && newVersionInfo != null
                && currentVersionInfo != null
                && VersionUtil.hasNewVersion(newVersionInfo.version, currentVersionInfo))
    }

    fun hasNewMonitorVersion(): Boolean {
        return hasNewVersion(VERSION_TYPE_MONITOR, mFirmwareVersionInfoLD.value)
    }

    fun hasNewSleeperVersion(): Boolean {
        return hasNewVersion(VERSION_TYPE_SLEEPER, mFirmwareVersionInfoLD.value)
    }

}