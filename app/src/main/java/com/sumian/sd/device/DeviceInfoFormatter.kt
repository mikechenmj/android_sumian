package com.sumian.sd.device

import com.blankj.utilcode.util.LogUtils
import com.sumian.hw.utils.SystemUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.bean.BlueDevice

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

        public fun getFormatedDeviceInfo(): String {
            //Device-Info": "app_version=速眠-test_1.2.3.1&model=iPhone10,3&system=iOS_11.3.1&monitor_fw=&monitor_sn=&sleeper_fw=&sleeper_sn="
            val monitorFw = formatMonitorInfo(getFormatMonitorFw())
            val monitorSn = formatMonitorInfo(DeviceManager.getMonitorSn())
            val sleeperFw = formatSpeedSleeperInfo(getFormatSleeperFw())
            val sleeperSn = formatSpeedSleeperInfo(DeviceManager.getSleeperSn())
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

        public fun getDeviceInfoMap(): Map<String, String> {
            val monitorFw = formatMonitorInfo(getFormatMonitorFw())
            val monitorSn = formatMonitorInfo(DeviceManager.getMonitorSn())
            val sleeperFw = formatSpeedSleeperInfo(getFormatSleeperFw())
            val sleeperSn = formatSpeedSleeperInfo(DeviceManager.getSleeperSn())
            return mapOf("system_ver" to systemVersion,
                    "monitor_fw" to monitorFw,
                    "sleeper_fw" to sleeperFw,
                    "monitor_sn" to monitorSn,
                    "sleeper_sn" to sleeperSn
            )
        }

        private fun getFormatMonitorFw(): String? {
            val monitor = DeviceManager.getMonitorLiveData().value ?: return null
            val channel = when (monitor.channelType) {
                BlueDevice.CHANNEL_TYPE_CLINIC -> "临床"
                BlueDevice.CHANNEL_TYPE_NORMAL -> "正式"
                else -> "null"
            }
            val bomVersion = monitor.sleeperBomVersion
            val bomVersionStr = if (bomVersion == null) "null" else "V$bomVersion"
            return "${monitor.version}-$channel-$bomVersionStr}"
        }

        private fun getFormatSleeperFw(): String? {
            val monitor = DeviceManager.getMonitorLiveData().value ?: return null
            val sleeperBomVersion = monitor.sleeperBomVersion
            val sleeperBomVersionStr = if (sleeperBomVersion == null) "null" else "V$sleeperBomVersion"
            return "${monitor.sleeperVersion}-$sleeperBomVersionStr}"
        }

        private fun formatMonitorInfo(monitorInfo: String?): String {
            val bluePeripheral = AppManager.getBlueManager().bluePeripheral
            if (bluePeripheral == null || !bluePeripheral.isConnected) {
                return ""
            }
            return monitorInfo ?: ""
        }

        private fun formatSpeedSleeperInfo(speedSleeperInfo: String?): String {
            val bluePeripheral = AppManager.getBlueManager().bluePeripheral
            if (bluePeripheral == null || !bluePeripheral.isConnected) {
                return ""
            }
            val monitor = DeviceManager.getMonitorLiveData().value
            return if (monitor == null || monitor.sleeperStatus != BlueDevice.STATUS_CONNECTED) {
                ""
            } else {
                speedSleeperInfo ?: ""
            }
        }
    }
}