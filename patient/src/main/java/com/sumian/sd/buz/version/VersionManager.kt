package com.sumian.sd.buz.version

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.version.bean.Version
import com.sumian.sd.buz.version.ui.AppUpgradeDialogActivity
import com.sumian.sd.buz.version.ui.DeviceUpgradeDialogActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.network.response.FirmwareVersionInfo
import com.sumian.sd.common.utils.EventBusUtil
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

    val mAppVersionInfo = MutableLiveData<Version>()
    private val mHandler = Handler(Looper.getMainLooper())

    init {
        queryAppVersion()
    }

    fun queryAppVersion(showDialogIfNeed: Boolean = false) {
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
                    mAppVersionInfo.value = response
                    it.version?.let {
                        val currentVersionCodes = currentVersion.split(".")
                        val onlineVersionCodes = it.split(".")
                        val isHaveUpgrade = VersionUtil.hasNewVersion(onlineVersionCodes, currentVersionCodes)
                        if (isHaveUpgrade) {
                            val mode = response.show_update_mode
                            mAppUpgradeMode.value = mode
                            if (showDialogIfNeed && mode != UPGRADE_MODE_MUTE) {
                                AppUpgradeDialogActivity.start(mode == UPGRADE_MODE_FORCE, response.description)
                            }
                        } else {
                            mAppUpgradeMode.value = UPGRADE_MODE_NO_UPGRADE
                        }
                    }
                }
            }
        })
    }

    fun delayQueryDeviceVersion() {
        mHandler.removeCallbacks(mDelayQueryDeviceVersionRunnable)
        mHandler.postDelayed(mDelayQueryDeviceVersionRunnable, 1000)
    }

    private val mDelayQueryDeviceVersionRunnable = Runnable { queryDeviceVersion(true) }

    fun queryDeviceVersion(showDialogIfNeed: Boolean = true) {
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
                    var hasNewSleeperVersion = hasNewSleeperVersion()
                    var hasNewMonitorVersion = hasNewMonitorVersion()
                    var sleeperUpgradeForce = response.sleeper?.isForceUpdate() ?: false
                    var monitorUpgradeForce = response.monitor?.isForceUpdate() ?: false
                    when {
                        hasNewMonitorVersion and monitorUpgradeForce -> {
                            DeviceUpgradeDialogActivity.start(VERSION_TYPE_MONITOR,
                                    monitorUpgradeForce, response.monitor?.description ?: "")
                        }
                        hasNewSleeperVersion and sleeperUpgradeForce -> {
                            DeviceUpgradeDialogActivity.start(VERSION_TYPE_SLEEPER,
                                    sleeperUpgradeForce, response.sleeper?.description ?: "")
                        }
                        hasNewMonitorVersion -> {
                            DeviceUpgradeDialogActivity.start(VERSION_TYPE_MONITOR,
                                    monitorUpgradeForce, response.monitor?.description ?: "")
                        }
                        hasNewSleeperVersion -> {
                            DeviceUpgradeDialogActivity.start(VERSION_TYPE_SLEEPER,
                                    sleeperUpgradeForce, response.sleeper?.description ?: "")
                        }
                        else -> {
                            EventBusUtil.postEvent(DeviceUpgradeDialogActivity.DfuUpgradeSuccessEvent())
                        }
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