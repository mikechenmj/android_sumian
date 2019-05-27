package com.sumian.device.event

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/10 15:17
 * desc   :
 * version: 1.0
 */
class Events {
    companion object {
        const val EVENT_RECEIVE_MONITOR_SN = "MonitorSn"
        const val EVENT_RECEIVE_MONITOR_VERSION_INFO = "MonitorVersionInfo"
        const val EVENT_RECEIVE_MONITOR_BATTERY = "MonitorBattery"

        const val EVENT_RECEIVE_SLEEP_MASTER_SN = "SleepMasterSn"
        const val EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO = "SleepMasterVersionInfo"
        const val EVENT_RECEIVE_SLEEP_MASTER_BATTERY = "SleepMasterBattery"

        const val EVENT_RECEIVE_SLEEP_MASTER_MAC = "SleepMasterMac"
        const val EVENT_RECEIVE_SLEEP_MASTER_CONNECT_STATUS = "SleepMasterConnectStatus"
        const val EVENT_RECEIVE_MONITOR_AND_SLEEP_MASTER_WORK_MODE = "MonitorAndSleepMasterWorkMode"
    }

}