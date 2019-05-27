package com.sumian.sd.buz.devicemanager

import com.blankj.utilcode.util.LogUtils
import com.sumian.device.data.MonitorChannel
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.app.App
import com.sumian.sd.common.utils.SystemUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 17:11
 * desc   :
 * version: 1.0
 */
class DeviceInfoFormatter {
    companion object {
        private const val WITH_SIGN = "&"
        private val systemVersion = SystemUtil.getSystemVersion()
        private val model = SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()
        private val appVersion = SystemUtil.getPackageInfo(App.getAppContext()).versionName

        fun getFormatedDeviceInfo(): String {
            //Device-Info": "app_version=速眠-test_1.2.3.1&model=iPhone10,3&system=iOS_11.3.1&monitor_fw=&monitor_sn=&sleeper_fw=&sleeper_sn="
            val device = DeviceManager.getDevice()
            val monitorFw = formatMonitorInfo(getFormatMonitorFw(device))
            val monitorSn = formatMonitorInfo(device?.monitorSn)
            val sleeperFw = formatSpeedSleeperInfo(getFormatSleeperFw(device))
            val sleeperSn = formatSpeedSleeperInfo(device?.sleepMasterSn)
            val deviceInfo = ("app_version=" + appVersion
                    + WITH_SIGN + "model=" + model
                    + WITH_SIGN + "system=" + systemVersion
                    + WITH_SIGN + "monitor_fw=" + monitorFw
                    + WITH_SIGN + "monitor_sn=" + monitorSn
                    + WITH_SIGN + "sleeper_fw=" + sleeperFw
                    + WITH_SIGN + "sleeper_sn=" + sleeperSn)
            LogUtils.d("device info", deviceInfo)
            return deviceInfo
        }

        fun getDeviceInfoMap(): Map<String, String> {
            val device = DeviceManager.getDevice()
            val monitorFw = formatMonitorInfo(getFormatMonitorFw(device))
            val monitorSn = formatMonitorInfo(device?.monitorSn)
            val sleeperFw = formatSpeedSleeperInfo(getFormatSleeperFw(device))
            val sleeperSn = formatSpeedSleeperInfo(device?.sleepMasterSn)
            return mapOf("system_ver" to systemVersion,
                    "monitor_fw" to monitorFw,
                    "sleeper_fw" to sleeperFw,
                    "monitor_sn" to monitorSn,
                    "sleeper_sn" to sleeperSn
            )
        }

        private fun getFormatMonitorFw(device: SumianDevice?): String? {
            val channel = when (device?.monitorVersionInfo?.channel) {
                MonitorChannel.CLINIC -> "临床"
                MonitorChannel.NORMAL -> "正式"
                else -> "null"
            }
            val bomVersion = device?.monitorVersionInfo?.hardwareVersion
            val bomVersionStr = if (bomVersion == null) "null" else "V$bomVersion"
            return "${device?.monitorVersionInfo?.softwareVersion}-$channel-$bomVersionStr"
        }

        private fun getFormatSleeperFw(device: SumianDevice?): String? {
            val sleeperBomVersion = device?.sleepMasterVersionInfo?.hardwareVersion
            val sleeperBomVersionStr = if (sleeperBomVersion == null) "null" else "V$sleeperBomVersion"
            return "${device?.sleepMasterVersionInfo?.softwareVersion}-$sleeperBomVersionStr"
        }

        private fun formatMonitorInfo(monitorInfo: String?): String {
            if (!DeviceManager.isMonitorConnected()) {
                return ""
            }
            return monitorInfo ?: ""
        }

        private fun formatSpeedSleeperInfo(speedSleeperInfo: String?): String {
            if (!DeviceManager.isMonitorConnected()) {
                return ""
            }
            val device = DeviceManager.getDevice()
            return if (device == null || !device.isSleepMasterConnected()) {
                ""
            } else {
                speedSleeperInfo ?: ""
            }
        }
    }
}