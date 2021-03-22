package com.sumian.device.cmd

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/8 16:17
 * desc   :
 *
 * version: 1.0
 */
class BleConstants {
    companion object {

        const val VERSION_CLINICAL = "0c"
        const val VERSION_NORMAL = "0e"
        const val VERSION_NORMAL_PRO = "0a"

        const val RESPONSE_CODE_POSITIVE = "01"

        // monitor
        const val MONITOR_CMD_HEADER = "55"
        const val APP_CMD_HEADER = "aa"

        const val RESPONSE_CODE_SUCCESS = "88"
        const val RESPONSE_CODE_NONE = "00"
        const val RESPONSE_CODE_FAIL = "ff"
        const val RESPONSE_CODE_FINISH = "fe"

        const val SET_MONITOR_TIME = "40"
        const val SET_USER_INFO = "4b"
        const val QUERY_MONITOR_BATTERY = "44"
        const val QUERY_MONITOR_SLEEP_MASTER_WORK_MODE = "61"
        const val QUERY_MONITOR_SN = "53"
        const val QUERY_MONITOR_VERSION = "50"
        const val SYNC_DATA = "4f"
        const val SYNC_START_OR_END = "8e"
        const val SYNC_TRANSPARENT = "8f"
        const val SYNC_SLEEP_DATA_CONTENT = "01"
        const val SYNC_SLEEP_MASTER_LOG_CONTENT = "02"
        const val MONITOR_ENTER_DFU = "51"

        // sleep master
        const val QUERY_SLEEP_MASTER_CONNECT_STATUS = "4e"
        const val QUERY_SLEEP_MASTER_BATTERY = "45"
        const val QUERY_SLEEP_MASTER_SN = "55"
        const val QUERY_SLEEP_MASTER_MAC = "56"
        const val QUERY_SLEEP_MASTER_VERSION = "54"
        const val SLEEP_MASTER_ENTER_DFU = "59"
        const val TOGGLE_SLEEP_MASTER_WORK_MODE =
                "58" //set 开启速眠仪的PA工作模式  note:速眠仪连接监测仪，同时满足下列条件可以开启PA功能：①佩戴状态 ②非睡状态
        const val CHANGE_SLEEP_MASTER = "52"
        const val SET_PATTERN = "4a"
        const val GET_PATTERN = "4c"
    }
}