package com.sumian.device.callback

import com.sumian.device.data.MonitorVersionInfo
import com.sumian.device.data.SleepMasterVersionInfo

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 16:44
 * desc   :
 * version: 1.0
 */
interface DeviceStatusListener {
    fun onStatusChange(type: String)
}

interface DeviceConnectStatusListener {
    fun onMonitorConnectStatusChange(status: Int)
    fun onSleepMasterConnectStateChange(status: Int)
}

interface DeviceBatteryStatusListener {
    fun onMonitorBatteryChange(battery: Int)
    fun onSleepMasterBatteryChange(battery: Int)
}

interface DeviceVersionStatusListener {
    fun onReceiveMonitorVersion(versionInfo: MonitorVersionInfo)

    fun onReceiveSleepMasterVersion(versionInfo: SleepMasterVersionInfo)

}

interface DeviceWorkModeStatusListener {
    fun onSleepMasterWorkModeChange(isOn: Boolean)
}