package com.sumian.device.manager.helper

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.sumian.device.R
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.MonitorChannel
import com.sumian.device.data.MonitorVersionInfo
import com.sumian.device.data.SleepMasterVersionInfo
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.util.BleCmdUtil
import com.sumian.device.util.LogManager
import com.sumian.device.util.MacUtil
import com.sumian.device.util.ThreadManager
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 18:15
 * desc   :
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("StaticFieldLeak")
object DeviceStateHelper {
    private lateinit var mContext: Context
    private const val DEFAULT_PROTOCOL_VERSION = 0

    fun init(context: Context): DeviceStateHelper {
        mContext = context.applicationContext
        DeviceManager.registerBleCommunicationWatcher(mBleCommunicationWatcher)
        return this
    }

    private val mBleCommunicationWatcher = object : BleCommunicationWatcher {
        override fun onRead(data: ByteArray, hexString: String) {
            if (!BleCmdUtil.isDeviceCmdValid(hexString)) {
                return
            }
            when (BleCmdUtil.getCmdType(hexString)) {
                BleCmd.QUERY_MONITOR_BATTERY // "44" 监测仪电量
                -> receiveMonitorBatteryInfo(hexString)
                BleCmd.QUERY_SLEEP_MASTER_BATTERY // "45" 速眠仪电量
                -> receiveSleepMasterBatteryInfo(hexString)
                BleCmd.QUERY_SLEEP_MASTER_CONNECT_STATUS // "4e" 速眠仪的连接状态
                -> receiveSleepMasterConnectionStatus(hexString)
                BleCmd.QUERY_MONITOR_SLEEP_MASTER_WORK_MODE // "61" 同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
                -> receiveAllMonitorAndSleepMasterStatus(data, hexString)
                else -> {
                }
            }

            when (BleCmdUtil.getCmdType(hexString)) {
                BleCmd.QUERY_MONITOR_BATTERY, // "44" 监测仪电量
                BleCmd.QUERY_SLEEP_MASTER_BATTERY, // "45" 速眠仪电量
                BleCmd.QUERY_SLEEP_MASTER_CONNECT_STATUS, // "4e" 速眠仪的连接状态
                BleCmd.QUERY_MONITOR_SLEEP_MASTER_WORK_MODE, // "61" 同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
                BleCmd.QUERY_MONITOR_VERSION, // 50
                BleCmd.QUERY_SLEEP_MASTER_VERSION // 54
                -> BleCommunicationController.makeSuccessResponse(hexString)
                else -> {
                }
            }
        }
    }

    fun syncState() {
        exeOneByOne(
                { setMonitorTime() },
                { setMonitorTime() }, // 特意多发一次，因为第一次会 gatt 发送失败
                { queryMonitorSn() },
                { queryMonitorVersion() },
                { queryMonitorBattery() },
                { querySleepMasterConnectStatus() },
                { SyncPatternHelper.syncPattern() },
                { ThreadManager.postToUIThread({ DeviceManager.startSyncSleepData() }, 1000) } // sync pattern 比较耗时，延时1s再同步数据
        )
    }

    private fun exeOneByOne(vararg tasks: () -> Unit) {
        for ((index, task) in tasks.withIndex()) {
            ThreadManager.postToUIThread(task, index * DeviceManager.WRITE_DATA_INTERVAL)
        }
    }

    private fun querySleepMasterBattery() {
        BleCommunicationController.requestByCmd(BleCmd.QUERY_SLEEP_MASTER_BATTERY)
    }

    private fun querySleepMasterConnectStatus() {
        BleCommunicationController.requestByCmd(BleCmd.QUERY_SLEEP_MASTER_CONNECT_STATUS)
    }

    private fun queryMonitorBattery() {
        BleCommunicationController.requestByCmd(BleCmd.QUERY_MONITOR_BATTERY)
    }

    private fun setMonitorTime(isRetry: Boolean = false) {
        BleCommunicationController.request(
                BleCmdUtil.createDataFromBytes(
                        BleCmd.SET_MONITOR_TIME,
                        createSetTimeData()
                ), object : BleRequestCallback {
            override fun onResponse(data: ByteArray, hexString: String) {
            }

            override fun onFail(code: Int, msg: String) {
                LogManager.log("setMonitorTime onFail: $msg")
            }
        })
    }

    private fun receiveMonitorBatteryInfo(cmd: String) {
        val battery = Integer.parseInt(BleCmdUtil.getContentFromData(cmd)!!, 16)
        LogManager.monitorLog("监测仪电量:$battery  cmd=$cmd")
        DeviceManager.postEvent(DeviceManager.EVENT_MONITOR_BATTERY_CHANGE, battery)
    }

    private fun receiveSleepMasterBatteryInfo(cmd: String) {
        val battery = Integer.parseInt(BleCmdUtil.getContentFromData(cmd)!!, 16)
        LogManager.sleepMasterLog("速眠仪电量:$battery  cmd=$cmd")
        DeviceManager.postEvent(DeviceManager.EVENT_SLEEP_MASTER_BATTERY_CHANGE, battery)
    }

    private fun receiveSetUserInfoResult(cmd: String) {
        if ("554b0188" == cmd) {
            LogManager.monitorLog("对设备设置 用户信息成功....$cmd")
        } else {
            LogManager.monitorLog("对设备设置 用户信息失败...$cmd")
        }
    }

    private fun receiveSleepMasterConnectionStatus(hexString: String) {
        val isConnected = BleCmdUtil.getContentFromData(hexString) == BleCmd.RESPONSE_CODE_POSITIVE
        if (isConnected) {
            exeOneByOne(
                    { querySleepMasterMac() },
                    { querySleepMasterSn() },
                    { querySleepMasterVersion() },
                    { querySleepMasterBattery() },
                    { queryMonitorAndSleepMasterWorkMode() }
            )
        }
        DeviceManager.postEvent(
                DeviceManager.EVENT_SLEEP_MASTER_CONNECT_STATUS_CHANGE,
                if (isConnected) DeviceConnectStatus.CONNECTED else DeviceConnectStatus.DISCONNECTED
        )
    }

    private fun receiveAllMonitorAndSleepMasterStatus(data: ByteArray, cmd: String) {
        //55 61 02 [aa] [bb]
        //aa 监测仪 监测模式 状态
        //bb 速眠仪 pa模式状态
        val monitorStatus = Integer.parseInt(cmd.substring(6, 8), 16)
        val sleepMasterStatus = Integer.parseInt(cmd.substring(8, 10), 16)
        val isMonitoring = monitorStatus > 0
        val isPa = sleepMasterStatus > 0
        LogManager.monitorLog("0x61 监测仪监测模式=$monitorStatus  cmd=$cmd")
        LogManager.sleepMasterLog("0x61 速眠仪工作模式=$sleepMasterStatus  cmd=$cmd")
        DeviceManager.postEvent(DeviceManager.EVENT_SLEEP_MASTER_WORK_MODE_CHANGE, isPa)
    }

    private fun getString(res: Int): String {
        return mContext.resources.getString(res)
    }

    private fun getVersionFromCmd(cmd: String, startIndex: Int, versionNumberCount: Int = 3): String? {
        return if (startIndex + 2 * versionNumberCount > cmd.length) {
            null
        } else {
            val sb = StringBuilder()
            for (i in 1..versionNumberCount) {
                if (i != 1) {
                    sb.append(".")
                }
                sb.append(cmdToInt(cmd, startIndex + 2 * (i - 1), startIndex + 2 * i))
            }
            sb.toString()
        }
    }

    private fun cmdToInt(cmd: String, startIndex: Int, endIndex: Int): Int {
        return Integer.parseInt(cmd.substring(startIndex, endIndex), 16)
    }

    fun createSetTimeData(): ByteArray {
        //2017-08-18 13:02:05/24   yyyy-mm-dd HH:mm:ss 24
        //0xaa4008 14 11 08 12 0f 13 01 18    //2017-08-18 15:22
        //0xAA4008 14 11 08 12 0F 16 2F 18
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        return byteArrayOf(
                Integer.parseInt(year.substring(0, 2), 10).toByte(),
                Integer.parseInt(year.substring(2), 10).toByte(),
                (calendar.get(Calendar.MONTH) + 1).toByte(),
                calendar.get(Calendar.DATE).toByte(),
                calendar.get(Calendar.HOUR_OF_DAY).toByte(),
                calendar.get(Calendar.MINUTE).toByte(),
                calendar.get(Calendar.SECOND).toByte(),
                0x18//24小时制
        )
    }

    fun toggleSleepMasterWorkMode(on: Boolean = true, callback: AsyncCallback<Any?>) {
        BleCommunicationController.request(
                BleCmdUtil.createDataFromString(
                        BleCmd.TOGGLE_SLEEP_MASTER_WORK_MODE,
                        BleCmd.RESPONSE_CODE_POSITIVE
                ), object : BleRequestCallback {
            override fun onResponse(data: ByteArray, hexString: String) {
                //aa58 01  默认 app 只能开启 pa 模式,不可以关闭
                //  Log.e(TAG, "onReceive: -----set   pa----->" + cmd);
                //5558 01 88/e0/e1/e2/e3/e4/e5/e6/e7/ff
                // 0x88 -- 设置成功
                // 0xE0 -- 监测仪未连接速眠仪
                // 0xE1 -- 监测仪未佩戴
                // 0xE2 -- 头部未在枕头上
                // 0xE3 -- 独立模式已开启,不能开启 pa 模式,提醒先关闭监测仪独立监测模式
                // 0xE4 -- 用户已经处于睡眠状态
                // 0xE5 -- 设置参数错误
                // 0xE6 -- 设置数据长度错误
                // 0xE7 -- 发送数据到速眠仪发生错误
                // 0xFF -- 未知错误
                val turnOnPaModeSuccess = BleCmdUtil.getContentFromData(hexString) == BleCmd.RESPONSE_CODE_SUCCESS
                if (turnOnPaModeSuccess) {
                    callback.onSuccess(null)
                    DeviceManager.postEvent(
                            DeviceManager.EVENT_SLEEP_MASTER_WORK_MODE_CHANGE,
                            true
                    )
                } else {
                    val errorMsgId = when (hexString.substring(6)) {
                        "e0" -> R.string.sleepy_not_connected_monitor
                        "e1" -> R.string.not_wear_monitor
                        "e2" -> R.string.head_not_at_pillow
                        "e3" -> R.string.not_turn_on_pa_mode
                        "e4" -> R.string.not_turn_on_pa_mode_in_sleep_time
                        "e8" -> R.string.monitor_is_charging
                        "e5", "e6", "e7", "ff" -> R.string.turn_on_sleepy_pa_mode_error_unknown
                        else -> R.string.turn_on_sleepy_pa_mode_error_unknown
                    }
                    val errorMessage = getString(errorMsgId)
                    DeviceManager.postEvent(
                            DeviceManager.EVENT_SLEEP_MASTER_WORK_MODE_CHANGE,
                            false
                    )
                    callback.onFail(1, errorMessage)
                }
                BleCommunicationController.makeSuccessResponse(hexString)
            }

            override fun onFail(code: Int, msg: String) {
                callback.onFail(2, msg)
            }
        })
    }


    private fun queryMonitorAndSleepMasterWorkMode() {
        BleCommunicationController.requestByCmd(BleCmd.QUERY_MONITOR_SLEEP_MASTER_WORK_MODE)
    }

    private fun querySleepMasterVersion() {
        // ```
        // A: aa 54
        // M: 55 54 xx [aaaaaa bbbbbb cccccccc pp]
        // ```
        // 字段|解释
        // ---|---
        // a| 速眠仪软件版本号
        // b| 速眠仪硬件版本号
        // c| 速眠仪头部检测算法版本号
        // p| 协议版本号
        BleCommunicationController.requestByCmd(
                BleCmd.QUERY_SLEEP_MASTER_VERSION,
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        val softwareVersion = getVersionFromCmd(hexString, 6)
                        val hardwareVersion = getVersionFromCmd(hexString, 12)
                        val headDetectAlgorithmVersion = getVersionFromCmd(hexString, 18, 4)
                        val protocolVersion = if (hexString.length >= 28) {
                            Integer.parseInt(hexString.substring(26), 16)
                        } else {
                            DEFAULT_PROTOCOL_VERSION
                        }
                        val versionInfo = SleepMasterVersionInfo(
                                softwareVersion,
                                hardwareVersion,
                                headDetectAlgorithmVersion,
                                protocolVersion
                        )
                        DeviceManager.postEvent(
                                DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO,
                                versionInfo
                        )
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    private fun querySleepMasterSn() {
        BleCommunicationController.requestByCmd(
                BleCmd.QUERY_SLEEP_MASTER_SN,
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        val sn = String(BleCmdUtil.getContentFromData(data)!!)
                        DeviceManager.postEvent(DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_SN, sn)
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    private fun querySleepMasterMac() {
        BleCommunicationController.requestByCmd(
                BleCmd.QUERY_SLEEP_MASTER_MAC,
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        DeviceManager.postEvent(
                                DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_MAC,
                                MacUtil.getStringMacFromCmdBytes(data)
                        )
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    private fun queryMonitorVersion() {
        // ```
        // A: aa 50
        // M: 55 50 xx [aaaaaa bb cccccc dddddd eeeeeeee pp]
        // ```
        // 字段|解释
        // ---|---
        // a| 软件版本, 每byte（无符号byte）代表版本号一段，格式形如 10.01.01
        // b| 渠道：临床0C， 正式0E
        // c| 硬件版本
        // d| 心率库版本
        // e| 睡眠算法版本号
        // p| 协议版本号
        BleCommunicationController.requestByCmd(
                BleCmd.QUERY_MONITOR_VERSION,
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        // 55 50 0e 【00 09 09】 【0e】 【43 31 31】 【00 00 42】 【30 30 30 4a】【pp】【软件版本】【临床0C/正式0E】【bom版本号】【心率库版本号】【睡眠算法版本号】【协议版本号】
                        val softwareVersion = getVersionFromCmd(hexString, 6)
                        val deviceChannel =
                                if (hexString.substring(
                                                12,
                                                14
                                        ).equals("0C")
                                ) MonitorChannel.CLINIC else MonitorChannel.NORMAL
                        val hardwareVersion = getVersionFromCmd(hexString, 14)
                        val heartBeatVersion = getVersionFromCmd(hexString, 20)
                        val sleepAlgorithmVersion = getVersionFromCmd(hexString, 26, 4)
                        val protocolVersion = if (hexString.length >= 36) {
                            Integer.parseInt(hexString.substring(34), 16)
                        } else {
                            DEFAULT_PROTOCOL_VERSION
                        }
                        DeviceManager.postEvent(
                                DeviceManager.EVENT_RECEIVE_MONITOR_VERSION_INFO,
                                MonitorVersionInfo(
                                        deviceChannel,
                                        softwareVersion,
                                        hardwareVersion,
                                        heartBeatVersion,
                                        sleepAlgorithmVersion,
                                        protocolVersion
                                )
                        )
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    private fun queryMonitorSn() {
        BleCommunicationController.requestByCmd(
                BleCmd.QUERY_MONITOR_SN,
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        val sn = String(BleCmdUtil.getContentFromData(data)!!)
                        DeviceManager.postEvent(DeviceManager.EVENT_RECEIVE_MONITOR_SN, sn)
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    private fun setUserInfo() {
        BleCommunicationController.request(
                BleCmdUtil.createDataFromString(BleCmd.SET_USER_INFO, "ffffffff"),
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        BleCommunicationController.makeSuccessResponse(hexString)
                    }

                    override fun onFail(code: Int, msg: String) {
                    }
                })
    }

    fun changeSleepMaster(sleepMasterSn: String?, callback: AsyncCallback<Any>) {
        if (TextUtils.isEmpty(sleepMasterSn)) {
            callback.onFail(1, "sn is empty")
            return
        }
        LogManager.monitorLog("绑定速眠仪： $sleepMasterSn")
        BleCommunicationController.request(
                BleCmdUtil.createDataFromBytes(
                        BleCmd.CHANGE_SLEEP_MASTER,
                        BleCmdUtil.stringToCharBytes(sleepMasterSn!!)
                ),
                object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        if (BleCmdUtil.getContentFromData(hexString) == BleCmd.RESPONSE_CODE_SUCCESS) {
                            callback.onSuccess()
                            BleCommunicationController.makeSuccessResponse(hexString)
                        } else {
                            callback.onFail(1, "绑定速眠仪失败,请重新扫码绑定")
                        }

                    }

                    override fun onFail(code: Int, msg: String) {
                        callback.onFail(code, msg)
                    }
                })
    }
}