package com.sumian.device.data

data class SumianDevice(
        var monitorConnectStatus: DeviceConnectStatus = DeviceConnectStatus.DISCONNECTED,
        var sleepMasterConnectStatus: DeviceConnectStatus = DeviceConnectStatus.DISCONNECTED,
        var monitorBattery: Int = 0,
        var sleepMasterBattery: Int = 0,
        var isSyncing: Boolean = false,
        var syncProgress: Int = 0,
        var syncTotalCount: Int = 1,
        var sleepMasterWorkModeStatus: SleepMasterWorkModeStatus = SleepMasterWorkModeStatus.OFF,
        var monitorSn: String? = null,
        var monitorMac: String? = null,
        var monitorVersionInfo: MonitorVersionInfo? = null,
        var sleepMasterSn: String? = null,
        var sleepMasterMac: String? = null,
        var sleepMasterVersionInfo: SleepMasterVersionInfo? = null
) {
    fun isMonitorConnected(): Boolean {
        return monitorConnectStatus == DeviceConnectStatus.CONNECTED
    }

    fun isSleepMasterConnected(): Boolean {
        return isMonitorConnected() && sleepMasterConnectStatus == DeviceConnectStatus.CONNECTED
    }

    fun isSleepMasterWorkModeOn(): Boolean {
        return sleepMasterWorkModeStatus == SleepMasterWorkModeStatus.ON
    }

    companion object {
        fun createByAddress(monitorMac: String): SumianDevice {
            return SumianDevice(monitorMac = monitorMac)
        }
    }
}