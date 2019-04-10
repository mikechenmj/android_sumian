package com.sumian.sd.buz.devicemanager.helper

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.annotation.StringRes
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.blue.model.BluePeripheral
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.buz.devicemanager.DeviceManager
import com.sumian.sd.buz.devicemanager.command.BlueCmd
import com.sumian.sd.buz.setting.version.VersionManager
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/1 15:51
 * desc   :
 * version: 1.0
 */
class DeviceStateHelper(deviceManager: DeviceManager) {
    companion object {
        const val SP_KEY_MONITOR_CACHE = "DeviceManager.MonitorCache"
        const val CMD_RESEND_TIME = 1000L * 5
        const val SHOW_UPGRADE_MONITOR_DIALOG_TIME = "SHOW_UPGRADE_MONITOR_DIALOG_TIME"
    }

    private val mDeviceManager = deviceManager
    private val mMonitorLiveData = mDeviceManager.getMonitorLiveData()
    private var mIsMonitoring: Boolean = false
    private val mMainHandler = Handler(Looper.getMainLooper())


    fun receiveAllMonitorAndSleeperStatus(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
        //byte1表示监测仪的监测模式状态
        //byte2表示速眠仪的 pa 模式状态
        //5561 xx 01 01
        val stateLen = Integer.parseInt(cmd.substring(4, 6), 16)
        val allState = cmd.substring(6)
        if (stateLen == (allState.length / 2)) {//判断所有状态是否一致
            peripheral.write(BlueCmd.cResponseOk(data[1]))
            val monitorSnoopingModeState = Integer.parseInt(allState.substring(0, 2), 16)
            //独立监测模式
            val isMonitoring = monitorSnoopingModeState == 0x01
            mIsMonitoring = isMonitoring
            val sleepyPaModeState = Integer.parseInt(allState.substring(2, 4), 16)
            mMonitorLiveData.value?.isMonitoring = isMonitoring
            val isPa = sleepyPaModeState > 0
//            mMonitorLiveData.value?.speedSleeper?.isPa = isPa
            mMonitorLiveData.value?.sleeperPaStatus = if (isPa) BlueDevice.PA_STATUS_PA else BlueDevice.PA_STATUS_NOT_PA
            LogManager.appendMonitorLog("0x61 收到监测仪的监测模式变化 监测模式=$monitorSnoopingModeState  cmd=$cmd")
            LogManager.appendSpeedSleeperLog("0x61 收到速眠仪的pa 模式变化  pa 模式=$sleepyPaModeState  cmd=$cmd")
            notifyMonitorChange()
        } else {//指令出错了,需重发所有状态
            peripheral.write(BlueCmd.cResponseFailed(data[1]))
            LogManager.appendMonitorLog("0x61  监测仪与速眠仪反馈模式变化的指令不正确  cmd=$cmd")
        }
    }

    fun receiveSleeperEnterPaModeResponse(cmd: String) {
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
        var isPa = false
        var errorMessage: String? = null
        if (cmd.length == 8) {
            when (cmd) {
                //开启pa成功
                "55580188" -> {
                    isPa = true
                    LogManager.appendSpeedSleeperLog("0x58 开启速眠仪的 pa 模式成功  cmd=$cmd")
                }
                else -> {
                    @StringRes val errorTextId: Int
                    val errorCode = cmd.substring(6)
                    errorTextId = when (errorCode) {
                        "e0" -> R.string.sleepy_not_connected_monitor
                        "e1" -> R.string.not_wear_monitor
                        "e2" -> R.string.head_not_at_pillow
                        "e3" -> R.string.not_turn_on_pa_mode
                        "e4" -> R.string.not_turn_on_pa_mode_in_sleep_time
                        "e8" -> R.string.monitor_is_charging
                        "e5", "e6", "e7", "ff" -> R.string.turn_on_sleepy_pa_mode_error
                        else -> R.string.turn_on_sleepy_pa_mode_error
                    }
                    errorMessage = App.getAppContext().resources.getString(errorTextId)
                    LogManager.appendSpeedSleeperLog("0x58 开始速眠的 pa 模式失败,原因是$errorMessage  cmd=$cmd")
                }
            }
        } else {
            errorMessage = App.getAppContext().resources.getString(R.string.turn_on_sleepy_pa_mode_error)
            LogManager.appendSpeedSleeperLog("0x58 开启速眠仪的 pa 模式失败,返回的指令长度不为8  cmd=$cmd")
        }
        if (isPa) {
            onTurnOnPaModeSuccess()
        } else {
            onTurnOnPaModeFailed(errorMessage
                    ?: "未知错误")
        }
        mMonitorLiveData.value?.sleeperPaStatus = if (isPa) BlueDevice.PA_STATUS_PA else BlueDevice.PA_STATUS_NOT_PA
        notifyMonitorChange()
    }

    fun receiveTurnOnOffMonitoringModeResponse(cmd: String) {
        //55 57 01  88
        //55 57 01  ff
        when (cmd) {
            "55570188"//操作成功
            -> {
                mIsMonitoring = !mIsMonitoring
                if (mIsMonitoring) {
                    LogManager.appendMonitorLog("0x57 开启监测仪的监测模式成功 cmd=$cmd")
                } else {
                    LogManager.appendMonitorLog("0x57 关闭监测仪的监测模式成功 cmd=$cmd")
                }
                mMonitorLiveData.value?.isMonitoring = mIsMonitoring
                notifyMonitorChange()
            }
            "555701ff"//操作失败
            -> {
                LogManager.appendMonitorLog("0x57 操作(开启/关闭)监测仪的监测模式失败  cmd=$cmd")
            }
            else -> {
                LogManager.appendMonitorLog("0x57 操作(开启/关闭)监测仪的监测模式失败  cmd=$cmd")
            }
        }
    }

    fun receiveSleeperMacInfo(cmd: String) {
        val mac = cmd.substring(6)
        val oldMac = java.lang.Long.parseLong(mac, 16)
        val newMac = (oldMac and 0xff) + ((oldMac shr 8) shl 8)
        val macSb = StringBuilder()
        macSb.delete(0, macSb.length)
        val hexString = java.lang.Long.toHexString(newMac)
        if (!TextUtils.isEmpty(hexString) && hexString.length >= 2) {
            var i = 0
            val len = hexString.length
            while (i < len) {
                if (i % 2 == 0) {
                    macSb.append(hexString.substring(i, i + 2))
                    if (i != len - 2) {
                        macSb.append(":")
                    }
                }
                i++
            }
        }
        val sleeperMac = macSb.toString().toUpperCase(Locale.getDefault())
        mMonitorLiveData.value?.sleeperMac = sleeperMac
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x56 获取到监测仪绑定的速眠仪的 mac address=$sleeperMac  cmd=$cmd")
        onLastDeviceDataReceived()
    }

    fun onLastDeviceDataReceived() {
        saveCacheFile()
        turnOffMonitoringModeIfNeeded()
    }

    fun saveCacheFile() {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, JsonUtil.toJson(mMonitorLiveData.value))
    }

    fun turnOffMonitoringModeIfNeeded() {
        if (mMonitorLiveData.value?.isMonitoring == true) {
            turnOnMonitoringMode(BlueDevice.MONITORING_CMD_CLOSE)
        }
    }

    fun turnOnMonitoringMode(monitoringMode: Int) {
        val bluePeripheral = mDeviceManager.getCurrentBluePeripheral() ?: return
        bluePeripheral.write(BlueCmd.cDoMonitorMonitoringMode(monitoringMode))
    }


    fun notifyMonitorChange() {
        mDeviceManager.notifyMonitorChange()
    }

    fun receiveMonitorBatteryInfo(cmd: String) {
        val monitorBattery = Integer.parseInt(cmd.substring(cmd.length - 2), 16)
        mMonitorLiveData.value?.battery = monitorBattery
        notifyMonitorChange()
        LogManager.appendMonitorLog("0x45 收到监测仪的电量变化---->$monitorBattery  cmd=$cmd")
    }

    fun receiveSyncTimeSuccessCmd() {
        LogManager.appendMonitorLog("收到0x40 监测仪校正时区成功")
    }

    fun onTransportChannelReady(peripheral: BluePeripheral) {
        peripheral.writeDelay(BlueCmd.cRTC(), 200)
        peripheral.writeDelay(BlueCmd.cMonitorBattery(), 400)
        peripheral.writeDelay(BlueCmd.cMonitorAndSleepyState(), 600)
        peripheral.writeDelay(BlueCmd.cSleepyConnectedState(), 800)
        peripheral.writeDelay(BlueCmd.cSleepyBattery(), 1000)
        peripheral.writeDelay(BlueCmd.cMonitorSnNumber(), 1200)
        //peripheral.writeDelay(BlueCmd.cSleepySnNumber(), 1400)
        peripheral.writeDelay(BlueCmd.cSleepyMac(), 1400)
        peripheral.writeDelay(BlueCmd.cMonitorFirmwareVersion(), 1600)
        peripheral.writeDelay(BlueCmd.cSleepyFirmwareVersion(), 1800)
        peripheral.writeDelay(BlueCmd.cUserInfo(), 2000)
        peripheral.writeDelay(BlueCmd.cSleepData(), 2200)
        mMainHandler.postDelayed(mQueryMonitorVersionDelayRunnable, CMD_RESEND_TIME)
        mMainHandler.postDelayed(mQuerySleeperVersionDelayRunnable, CMD_RESEND_TIME)
        LogManager.appendMonitorLog("连接成功,开始初始化同步监测仪与速眠仪相关状态数据 " + peripheral.name)
    }

    val mQueryMonitorVersionDelayRunnable: Runnable by lazy {
        Runnable {
            if (!mDeviceManager.isMonitorConnected()) return@Runnable
            val peripheral = mDeviceManager.getCurrentBluePeripheral()
                    ?: return@Runnable
            if (mMonitorLiveData.value?.version == null) {
                peripheral.writeDelay(BlueCmd.cMonitorFirmwareVersion(), 0)
                mMainHandler.postDelayed(mQueryMonitorVersionDelayRunnable, CMD_RESEND_TIME)
                LogManager.appendMonitorLog("重新请求 设备版本信息")
                LogUtils.d("重新请求 设备版本信息")
            }
        }
    }

    val mQuerySleeperVersionDelayRunnable: Runnable by lazy {
        Runnable {
            if (!isMonitorConnected() || mMonitorLiveData.value?.isSleeperConnected != true) return@Runnable
            val peripheral = getCurrentBluePeripheral()
                    ?: return@Runnable
            if (mMonitorLiveData.value?.sleeperVersion == null) {
                peripheral.writeDelay(BlueCmd.cSleepyFirmwareVersion(), 0)
                mMainHandler.postDelayed(mQuerySleeperVersionDelayRunnable, CMD_RESEND_TIME)
                LogManager.appendMonitorLog("重新请求 监测仪版本信息")
                LogUtils.d("重新请求 监测仪版本信息")
            }
        }
    }

    fun isMonitorConnected(): Boolean {
        return mDeviceManager.isMonitorConnected()
    }

    fun onTurnOnPaModeSuccess() {
        mDeviceManager.onTurnOnPaModeSuccess()
    }

    fun onTurnOnPaModeFailed(message: String) {
        mDeviceManager.onTurnOnPaModeFailed(message)
    }

    fun getCurrentBluePeripheral(): BluePeripheral? {
        return mDeviceManager.getCurrentBluePeripheral()
    }

    fun receiveSleeperBatteryInfo(cmd: String) {
        val sleepyBattery = Integer.parseInt(cmd.substring(cmd.length - 2), 16)
        mMonitorLiveData.value?.sleeperBattery = sleepyBattery
        notifyMonitorChange()
        LogManager.appendMonitorLog("0x44 收到速眠仪的电量变化---->$sleepyBattery  cmd=$cmd")
    }

    fun receiveSetUserInfoResult(cmd: String) {
        if ("554b0188" == cmd) {
            LogManager.appendMonitorLog("对设备设置 用户信息成功....$cmd")
        } else {
            LogManager.appendMonitorLog("对设备设置 用户信息失败...$cmd")
        }
    }

    fun receiveSleeperConnectionStatus(peripheral: BluePeripheral, cmd: String) {
        val sleepyConnectState = Integer.parseInt(cmd.substring(cmd.length - 2), 16)
        if (sleepyConnectState == 0x00) {
            mMonitorLiveData.value?.sleeperStatus = BlueDevice.STATUS_UNCONNECTED
            mMonitorLiveData.value?.sleeperBattery = 0
        } else {
            mMonitorLiveData.value?.sleeperStatus = BlueDevice.STATUS_CONNECTED
            peripheral.writeDelay(BlueCmd.cSleepySnNumber(), 100)
            peripheral.writeDelay(BlueCmd.cSleepyBattery(), 700)
        }
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x4e 收到速眠仪的连接状态变化------>$sleepyConnectState  cmd=$cmd")
    }

    /**
     * v1:  55 50 03 【09 05 01】
     * v2:  55 50 07 【00 09 09】 【0e】 【0b 00 00】【软件版本】【临床0C/正式0E】【bom版本号】
     * v3:  55 50 0e 【00 09 09】 【0e】 【43 31 31】 【00 00 42】 【30 30 30 4a】【软件版本】【临床0C/正式0E】【bom版本号】【心率库版本号】【睡眠算法版本号】
     */
    fun receiveMonitorVersionInfo(cmd: String) {
        // 55 50 03 【09 05 01】 老版本
        // 55 50 07 【01 00 02】 【0E】 【08 00 00】 新版本 【软件版本】【临床0C/正式0E】【bom版本号】
        val monitorFirmwareVersion = getVersionFromCmd(cmd, 6)
        mMonitorLiveData.value?.version = monitorFirmwareVersion
        if (cmd.length >= 20) {
            mMonitorLiveData.value?.channelType = if (cmd.substring(12, 14).equals("0C")) BlueDevice.CHANNEL_TYPE_CLINIC else BlueDevice.CHANNEL_TYPE_NORMAL
            mMonitorLiveData.value?.bomVersion = getVersionFromCmd(cmd, 14)
            if (cmd.length >= 34) {
                mMonitorLiveData.value?.heartBeatVersion = getVersionFromCmd(cmd, 20)
                mMonitorLiveData.value?.sleepAlgorithmVersion = cmd.substring(26)
            }
        }
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x50 监测仪的固件版本信息$monitorFirmwareVersion  cmd=$cmd")
        getAndCheckFirmVersion()
    }

    fun cmdToInt(cmd: String, startIndex: Int, endIndex: Int): Int {
        return Integer.parseInt(cmd.substring(startIndex, endIndex), 16)
    }

    fun getVersionFromCmd(cmd: String, startIndex: Int): String {
        if (startIndex + 6 > cmd.length) {
            return ""
        } else {
            return "${cmdToInt(cmd, startIndex, startIndex + 2)}.${cmdToInt(cmd, startIndex + 2, startIndex + 4)}.${cmdToInt(cmd, startIndex + 4, startIndex + 6)}"
        }
    }

    fun getAndCheckFirmVersion() {
        mMainHandler.removeCallbacks(mDelayCheckFirmwareVersionRunnable)
        // 延时，防止先后获取监测仪、速眠仪版本信息重复查询。
        mMainHandler.postDelayed(mDelayCheckFirmwareVersionRunnable, 3000)
    }

    private val mDelayCheckFirmwareVersionRunnable = Runnable { VersionManager.getAndCheckFirmVersionShowUpgradeDialogIfNeed(true) }


    fun receiveMonitorSnInfo(data: ByteArray, cmd: String) {
        val monitorSn = BlueCmd.formatSn(data)
        LogManager.appendMonitorLog("0x53 获取到监测仪的sn=$monitorSn  cmd=$cmd")
        mMonitorLiveData.value?.sn = monitorSn
        uploadDeviceSns(monitorSn = monitorSn)
        notifyMonitorChange()
    }

    private fun uploadDeviceSns(monitorSn: String? = null, sleeperSn: String? = null) {
        if (monitorSn == null && sleeperSn == null) {
            return
        }
        val map = HashMap<String, String>()
        monitorSn?.let { map.put("monitor_sn", it) }
        sleeperSn?.let { map.put("sleeper_sn", it) }
        if (monitorSn != null || sleeperSn != null) {
            AppManager.getSdHttpService().modifyUserProfile(map).enqueue(object : BaseSdResponseCallback<UserInfo>() {
                override fun onSuccess(response: UserInfo?) {
                    LogUtils.d(response)
                    AppManager.getAccountViewModel().updateUserInfo(response)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    LogUtils.d(errorResponse.message)
                }
            })
        }
    }

    /**
     * v1:  55 54 06 【xx xx xx】
     * v2:  55 54 06 【xx xx xx】 【yy yy yy】
     * v3:  55 54 06 【xx xx xx】 【yy yy yy】 【zz zz zz】【速眠仪软件版本号】【速眠仪硬件版本号】【速眠仪头部检测算法版本号】
     */
    fun receiveSleeperVersionInfo(cmd: String) {
        val sleepyFirmwareVersion = getVersionFromCmd(cmd, 6)
        mMonitorLiveData.value?.sleeperVersion = sleepyFirmwareVersion
        if (cmd.length >= 18) {
            mMonitorLiveData.value?.sleeperBomVersion = getVersionFromCmd(cmd, 12)
            if (cmd.length >= 24) {
                mMonitorLiveData.value?.sleeperHeadMonitorAlgorithmVersion = getVersionFromCmd(cmd, 16)
            }
        }
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x54 速眠仪的固件版本信息$sleepyFirmwareVersion  cmd=$cmd")
        getAndCheckFirmVersion()
    }

    fun receiveSleeperSnInfo(data: ByteArray, cmd: String) {
        val sleepySn = BlueCmd.formatSn(data)
        mMonitorLiveData.value?.sleeperSn = sleepySn
        notifyMonitorChange()
        uploadDeviceSns(sleeperSn = sleepySn)
        LogManager.appendSpeedSleeperLog("0x55 获取到监测仪绑定的速眠仪的 sn=$sleepySn  cmd=$cmd")
    }

    fun turnOnSleeperPaMode() {
        val bluePeripheral = getCurrentBluePeripheral() ?: return
        bluePeripheral.writeDelay(BlueCmd.cDoSleepyPaMode(), 500)
        onTurnOnPaModeStart()
        LogManager.appendSpeedSleeperLog("主动 turn on  速眠仪 pa 模式")
    }

    fun onTurnOnPaModeStart() {
        mDeviceManager.onTurnOnPaModeStart()
    }


}