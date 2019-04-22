@file:Suppress("MemberVisibilityCanBePrivate")

package com.sumian.sd.buz.devicemanager

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.blue.callback.BlueAdapterCallback
import com.sumian.blue.callback.BluePeripheralCallback
import com.sumian.blue.callback.BluePeripheralDataCallback
import com.sumian.blue.constant.BlueConstant
import com.sumian.blue.model.BluePeripheral
import com.sumian.blue.model.bean.BlueUuidConfig
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.devicemanager.command.BlueCmd
import com.sumian.sd.buz.devicemanager.helper.DeviceStateHelper
import com.sumian.sd.buz.devicemanager.helper.SyncDeviceDataHelper
import com.sumian.sd.buz.devicemanager.pattern.SyncPatternService
import com.sumian.sd.buz.devicemanager.uploadsleepdata.SleepDataUploadHelper
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.utils.SystemUtil
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:42
 * desc   :
 * version: 1.0
 */
object DeviceManager : BlueAdapterCallback, MonitorEventListener {

    private const val SP_KEY_MONITOR_CACHE = "DeviceManager.MonitorCache"
    private const val MAX_CONNECT_RETRY_TIMES = 3

    private var mPackageNumber: Int = 1     // 透传数据 包的index
    private var mIsUnbinding: Boolean = false
    private val mMonitorLiveData = MutableLiveData<BlueDevice>()
    private val mIsBluetoothEnableLiveData = MutableLiveData<Boolean>()
    private val mMonitorEventListeners = HashSet<MonitorEventListener>()
    private val mIsUploadingSleepDataToServerLiveData = MutableLiveData<Boolean>()
    private val mSyncDataHelper = SyncDeviceDataHelper(this)
    private val mDeviceStateHelper = DeviceStateHelper(this)
    private var mConnectMonitor: BlueDevice? = null     // 等待被连接的monitor，用于重连
    private var mConnectRetryTimes = 0  // 连接失败重连次数
    private val mPhoneInfo = SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()

    fun init(context: Context) {
        AppManager.getBlueManager().addBlueAdapterCallback(this)
        mIsBluetoothEnableLiveData.value = AppManager.getBlueManager().isEnable
        val monitorCache = getCachedMonitor()
        setMonitorToLiveData(monitorCache)
        SleepDataUploadHelper.getInstance().init(context)
    }

    fun getMonitorLiveData(): MutableLiveData<BlueDevice> {
        return mMonitorLiveData
    }

    fun getMonitorSn(): String? {
        return mMonitorLiveData.value?.sn
    }

    fun getSleeperSn(): String? {
        return mMonitorLiveData.value?.sleeperSn
    }

    fun getMonitorMac(): String? {
        return mMonitorLiveData.value?.mac
    }

    fun getSleeperMac(): String? {
        return mMonitorLiveData.value?.sleeperMac
    }

    fun setSleeperMac(mac: String) {
        mMonitorLiveData.value?.sleeperMac = mac
        notifyMonitorChange()
    }

    fun getSleeperDfuMac(): String {
        //CD9DC408D89D
        val sleepyMac = getSleeperMac() ?: ""
        //由于 dfu 升级需要设备 mac+1
        //uint64 x old mac;, y new mac;
        // y = (( x & 0xFF ) + 1) + ((x >> 8) << 8);
        val oldMac = java.lang.Long.parseLong(sleepyMac, 16)
        val newMac = (oldMac and 0xff) + 1 + (oldMac shr 8 shl 8)
        val macSb = StringBuilder()
        //  macSb.delete(0, macSb.length());
        val hexString = java.lang.Long.toHexString(newMac)
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
        return macSb.toString().toUpperCase(Locale.getDefault())
    }

    private fun formatVersion(version: String?): String? {
        return if (version == null || version == "0.0.0") {
            null
        } else version

    }

    fun getMonitorVersion(): String? {
        return formatVersion(mMonitorLiveData.value?.version)
    }

    fun getSleeperVersion(): String? {
        return formatVersion(mMonitorLiveData.value?.sleeperVersion)
    }

    fun getMonitorBomVersion(): String? {
        return formatVersion(mMonitorLiveData.value?.bomVersion)
    }

    fun getSleeperBomVersion(): String? {
        return formatVersion(mMonitorLiveData.value?.sleeperBomVersion)
    }

    fun getMonitorStatus(): Int {
        return mMonitorLiveData.value?.status ?: BlueDevice.STATUS_UNCONNECTED
    }

    fun getSleeperStatus(): Int {
        return mMonitorLiveData.value?.sleeperStatus ?: BlueDevice.STATUS_UNCONNECTED
    }

    fun getMonitorBattery(): Int {
        return mMonitorLiveData.value?.battery ?: 0
    }

    fun getSleeperBattery(): Int {
        return mMonitorLiveData.value?.sleeperBattery ?: 0
    }

    fun getIsBluetoothEnableLiveData(): MutableLiveData<Boolean> {
        return mIsBluetoothEnableLiveData
    }

    fun isBluetoothEnable(): Boolean {
        return mIsBluetoothEnableLiveData.value ?: false
    }

    fun addMonitorEventListener(listener: MonitorEventListener) {
        mMonitorEventListeners.add(listener)
    }

    fun removeMonitorEventListener(listener: MonitorEventListener) {
        mMonitorEventListeners.remove(listener)
    }

    private fun getCachedMonitor(): BlueDevice? {
        return JsonUtil.fromJson(SPUtils.getInstance().getString(SP_KEY_MONITOR_CACHE), BlueDevice::class.java)
    }

    fun tryToConnectCacheMonitor() {
        if (isMonitorConnected() || isMonitorConnecting() || !isBluetoothEnable()) {
            return
        }
        getCachedMonitor()?.let {
            connectMonitor(it)
        }
    }

    private var mStartConnectTime = 0L // log connect time

    fun connectMonitor(monitor: BlueDevice) {
        connectMonitor(monitor, false)
    }

    /**
     * 断开蓝牙后直接connect连不上，需要先扫描，再连接
     */
    private fun connectMonitor(monitor: BlueDevice, isRetry: Boolean = false) {
        if (!isRetry) {
//            StatUtil.trackCustomBeginKVEvent(StatConstants.connect_device)
            mStartConnectTime = System.currentTimeMillis()
            mConnectRetryTimes = 0
            onConnectStart()
        }
        mConnectMonitor = monitor
        AppManager.getBlueManager().clearBluePeripheral()
        val remoteDevice = AppManager.getBlueManager().getBluetoothDeviceFromMac(monitor.mac)
        if (remoteDevice != null) {
            val blueUuidConfig = BlueUuidConfig()
            blueUuidConfig.serviceUuid = BlueConstant.SERVICE_UUID
            blueUuidConfig.notifyUuid = BlueConstant.NOTIFY_UUID
            blueUuidConfig.writeUuid = BlueConstant.WRITE_UUID
            blueUuidConfig.descUuid = BlueConstant.DESCRIPTORS_UUID
            val bluePeripheral = BluePeripheral.PeripheralBlueBuilder()
                    .setContext(App.getAppContext())
                    .setBlueUuidConfig(blueUuidConfig)
                    .setName(remoteDevice.name)
                    .setRemoteDevice(remoteDevice)
                    .bindWorkThread(AppManager.getBlueManager().workThread)
                    .build()
            bluePeripheral.addPeripheralDataCallback(mPeripheralDataCallback)
            bluePeripheral.addPeripheralCallback(mPeripheralCallback)
            bluePeripheral.connect()
            monitor.status = BlueDevice.STATUS_CONNECTING
            setMonitorToLiveData(monitor)
            LogManager.appendMonitorLog("主动连接监测仪  connect to   name=" + remoteDevice.name + "  address=" + remoteDevice.address)
        } else {
            LogManager.appendMonitorLog("主动连接监测仪  connect to  is invalid   because  init bluetoothDevice is null")
        }
    }

    private val mPeripheralCallback = object : BluePeripheralCallback {
        override fun onConnecting(peripheral: BluePeripheral, connectState: Int) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_CONNECTING
            mMonitorLiveData.value?.battery = 0
            mMonitorLiveData.value?.resetSleeper()
            notifyMonitorChange()
            LogManager.appendMonitorLog("监测仪正在连接中 " + peripheral.name)
        }

        override fun onConnectSuccess(peripheral: BluePeripheral, connectState: Int) {
            LogManager.appendMonitorLog("bt connect onConnectSuccess time: ${System.currentTimeMillis() - mStartConnectTime}")
            mMonitorLiveData.value?.status = BlueDevice.STATUS_CONNECTED
            notifyMonitorChange()
            onConnectSuccess()
            AppManager.getBlueManager().saveBluePeripheral(peripheral)
            LogManager.appendMonitorLog("监测仪连接成功，耗时${System.currentTimeMillis() - mStartConnectTime}ms")
            StatUtil.event(StatConstants.connect_device, mapOf(
                    "connect_time" to (System.currentTimeMillis() - mStartConnectTime).toString(),
                    "phone_info" to mPhoneInfo,
                    "result" to "success"))
//            StatUtil.trackCustomEndKVEvent(StatConstants.connect_device)
        }

        override fun onConnectFailed(peripheral: BluePeripheral, connectState: Int) {
            if (mConnectRetryTimes > MAX_CONNECT_RETRY_TIMES) {
                if (isSyncing()) {
                    mMonitorLiveData.value?.isSyncing = false
                    onSyncFailed()
                }
                onConnectFailed()
                AppManager.getBlueManager().refresh()
                LogManager.appendMonitorLog("监测仪连接失败，耗时${System.currentTimeMillis() - mStartConnectTime}ms")
                StatUtil.event(StatConstants.connect_device, mapOf(
                        "connect_time" to (System.currentTimeMillis() - mStartConnectTime).toString(),
                        "phone_info" to mPhoneInfo,
                        "result" to "fail"))
//                StatUtil.trackCustomEndKVEvent(StatConstants.connect_device)
            } else {
                LogManager.appendMonitorLog("监测仪连接失败 尝试重连 第 ${mConnectRetryTimes + 1} 次 ${peripheral.name}")
                connectMonitor(mConnectMonitor ?: return, true)
            }
        }

        override fun onDisconnecting(peripheral: BluePeripheral, connectState: Int) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
            mMonitorLiveData.value?.battery = 0
            mMonitorLiveData.value?.resetSleeper()
            notifyMonitorChange()
            AppManager.getBlueManager().refresh()
            LogManager.appendMonitorLog("监测仪正在断开连接 " + peripheral.name)
        }

        override fun onDisconnectSuccess(peripheral: BluePeripheral, connectState: Int) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
            mMonitorLiveData.value?.battery = 0
            mMonitorLiveData.value?.resetSleeper()
            notifyMonitorChange()
            mIsUnbinding = false
            if (mIsUnbinding) {
                LogManager.appendMonitorLog("解绑监测仪成功 " + peripheral.name)
            } else {
                LogManager.appendMonitorLog("监测仪成功断开连接 " + peripheral.name)
            }
            AppManager.getBlueManager().refresh()
            AppManager.getBlueManager().clearBluePeripheral()
        }

        override fun onTransportChannelReady(peripheral: BluePeripheral) {
            mDeviceStateHelper.onTransportChannelReady(peripheral)
        }
    }

    private val mPeripheralDataCallback = object : BluePeripheralDataCallback {
        override fun onSendSuccess(bluePeripheral: BluePeripheral, data: ByteArray) {
            val cmd = BlueCmd.bytes2HexString(data)
            if (BlueCmd.formatCmdIndex(cmd) != "8f") {
                LogManager.appendBluetoothLog("蓝牙发送成功的指令  cmd=$cmd")
            }
            when (cmd) {
                "aa4f0101" -> LogManager.appendMonitorLog("0x4f 主动同步睡眠特征数据指令成功")
                "aa570101" -> LogManager.appendMonitorLog("0x57 主动 turn on 监测仪的监测模式发送指令成功")
                "aa570100" -> LogManager.appendMonitorLog("0x57 主动 turn off 监测仪的监测模式发送指令成功")
                "aa580101" -> LogManager.appendSpeedSleeperLog("0x58 turn on 速眠仪的 pa 模式发送指令成功")
                else -> Unit
            }
        }

        override fun onReceiveSuccess(peripheral: BluePeripheral, data: ByteArray) {
            val cmd = BlueCmd.bytes2HexString(data)
            if (TextUtils.isEmpty(cmd) || cmd.length <= 2 || "55" != cmd.substring(0, 2)) {
                //设备命令出问题
                //不是设备命令,有可能发生粘包,分包,拆包现象. 需要重新发送该命令,再次请求消息
                return
            }
            val cmdIndex = BlueCmd.formatCmdIndex(cmd)
            when (cmdIndex) {
                // sync data
                "4f"//主动获取睡眠特征数据
                -> mSyncDataHelper.receiveRequestSleepDataResponse(cmd)
                "8e" // 开始/结束 透传数据
                -> mSyncDataHelper.receiveStartOrFinishTransportCmd(peripheral, data, cmd)
                "8f" // 透传数据
                -> mSyncDataHelper.receiveSleepData(peripheral, data, cmd)

                // status
                "40"//校正时区
                -> mDeviceStateHelper.receiveSyncTimeSuccessCmd()
                "44"//获取监测仪电量
                -> mDeviceStateHelper.receiveMonitorBatteryInfo(cmd)
                "45"//获取速眠仪电量
                -> mDeviceStateHelper.receiveSleeperBatteryInfo(cmd)
                "4b" -> mDeviceStateHelper.receiveSetUserInfoResult(cmd)
                "4e"//获取速眠仪的连接状态
                -> mDeviceStateHelper.receiveSleeperConnectionStatus(peripheral, cmd)
                "50"//获取监测仪固件版本信息
                -> mDeviceStateHelper.receiveMonitorVersionInfo(cmd)
                "52" -> LogManager.appendBluetoothLog("0x52 正在绑定速眠仪中,$cmd")
                "53"//获取监测仪的 sn 号
                -> mDeviceStateHelper.receiveMonitorSnInfo(data, cmd)
                "54"//获取速眠仪的固件版本信息
                -> mDeviceStateHelper.receiveSleeperVersionInfo(cmd)
                "55"//获取监测仪绑定的并且连接着的速眠仪的 sn 号
                -> mDeviceStateHelper.receiveSleeperSnInfo(data, cmd)
                "56"//获取监测仪绑定的速眠仪的 mac 地址
                -> mDeviceStateHelper.receiveSleeperMacInfo(cmd)
                "57"//开启/关闭监测仪的监测模式  0x01 开启  0x00 关闭
                -> mDeviceStateHelper.receiveTurnOnOffMonitoringModeResponse(cmd)
                "58"//使速眠仪进入 pa 模式之后的反馈
                -> mDeviceStateHelper.receiveSleeperEnterPaModeResponse(cmd)
                "61"//同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
                -> mDeviceStateHelper.receiveAllMonitorAndSleeperStatus(peripheral, data, cmd)

                // dfu
                "59"//使速眠仪进入 dfu 模式开启成功
                -> receiveSleeperEnterDfuResponse(cmd)
                "51"//监测仪自己固件 dfu 模式开启成功
                -> receiveMonitorEnterDfuResponse(cmd)


                "d0"//临床原始数据采集时间点
                -> {
                    //val unixTime = java.lang.Long.parseLong(cmd.substring(4, 12), 16)
                }
                "d1"//采集临床肌电数据   不回响应包
                -> {
                    //val emg = BlueByteUtil.formatData(data)
                }
                "d2"//采集临床脉率数据   不回响应包
                -> {
                    //val pulse = BlueByteUtil.formatData(data)
                }
                "d3"//采集临床加速度数据  不回响应包
                -> {
                    //val speed = BlueByteUtil.formatData(data)
                }
                "4a", "4c" -> {
                }
                else -> peripheral.write(BlueCmd.cResponseOk(data[1]))
            }
        }
    }

    fun turnOnSleeperPaMode() {
        mDeviceStateHelper.turnOnSleeperPaMode()
    }

    fun syncSleepData() {
        if (isSyncing()) return
        val bluePeripheral = getCurrentBluePeripheral() ?: return
        if (mMonitorLiveData.value?.isSyncing == true) return
        bluePeripheral.write(BlueCmd.cSleepData())
        mPackageNumber = 1
        LogManager.appendTransparentLog("主动同步睡眠数据")
        notifyMonitorChange()
    }

    fun unbind() {
        setMonitorToLiveData(null)
        mIsUnbinding = true
        getCurrentBluePeripheral()?.close()
        clearCacheDevice()
        AppManager.getBlueManager().clearBluePeripheral()
        AppManager.getBlueManager().stopScanForDevice()
        LogManager.appendMonitorLog("主动解绑监测仪")
    }

    fun isMonitorConnected(): Boolean {
        return mMonitorLiveData.value?.isConnected ?: false
    }

    fun isMonitorConnecting(): Boolean {
        return mMonitorLiveData.value?.status == BlueDevice.STATUS_CONNECTING
    }

    fun isSleeperConnected(): Boolean {
        return mMonitorLiveData.value?.sleeperStatus == BlueDevice.STATUS_CONNECTED
    }

    fun isSyncing(): Boolean {
        return mMonitorLiveData.value?.isSyncing ?: false
    }

    override fun onAdapterEnable() {
        mIsBluetoothEnableLiveData.value = true
        tryToConnectCacheMonitor()
        LogManager.appendBluetoothLog("蓝牙 turn on")
    }

    override fun onAdapterDisable() {
        mIsBluetoothEnableLiveData.value = false
        AppManager.getBlueManager().clearBluePeripheral()
        AppManager.getBlueManager().stopScanForDevice()
        mMonitorLiveData.value?.resetSleeper()
        LogManager.appendBluetoothLog("蓝牙 turn off")
    }


    private fun receiveMonitorEnterDfuResponse(cmd: String) {
        if ("88".equals(cmd.substring(6, 8))) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
            mMonitorLiveData.value?.battery = 0
            mMonitorLiveData.value?.resetSleeper()
            notifyMonitorChange()
            LogManager.appendSpeedSleeperLog("0x51 监测仪进入dfu 模式成功  cmd=$cmd")
        } else {
            LogManager.appendSpeedSleeperLog("0x51 监测仪进入dfu 模式失败  cmd=$cmd")
        }
    }

    private fun receiveSleeperEnterDfuResponse(cmd: String) {
        if ("88".equals(cmd.substring(6, 8))) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
            mMonitorLiveData.value?.sleeperBattery = 0
            mMonitorLiveData.value?.resetSleeper()
            notifyMonitorChange()
            LogManager.appendMonitorLog("0x59 速眠仪进入dfu 模式成功 cmd=$cmd")
        } else {
            LogManager.appendMonitorLog("0x59 速眠仪进入dfu 模式失败 cmd=$cmd")
        }
    }


    /**
     * @param peripheral       peripheral
     * @param data             data
     * @param readyForNextData true 准备接受, false 异常
     */
    fun writeResponse(peripheral: BluePeripheral, data: ByteArray, readyForNextData: Boolean) {
        val command = byteArrayOf(0xaa.toByte(), 0x8e.toByte(), data[2], data[3], data[4], data[5], data[6], data[7], data[8], if (readyForNextData) 0x88.toByte() else 0xff.toByte())
        peripheral.write(command)
    }


    fun cacheBlueDevice(blueDevice: BlueDevice) {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, JsonUtil.toJson(blueDevice))
    }

    private fun clearCacheDevice() {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, "")
        LogManager.appendUserOperationLog("设备被成功解绑,并清除掉缓存成功")
    }

    fun getCurrentBluePeripheral(): BluePeripheral? {
        val bluePeripheral = AppManager.getBlueManager().bluePeripheral
        return if (bluePeripheral == null || !bluePeripheral.isConnected) {
            null
        } else bluePeripheral
    }

    fun notifyMonitorChange() {
        // LiveData 调用set时会下发数据
        setMonitorToLiveData(mMonitorLiveData.value)
    }

// ---------------- monitor event listener start ----------------

    override fun onSyncStart() {
        mMonitorLiveData.value?.isSyncing = true
        notifyMonitorChange()
        for (listener in mMonitorEventListeners) {
            listener.onSyncStart()
        }
    }

    override fun onSyncProgressChange(packageNumber: Int, packageProgress: Int, packageTotalCount: Int) {
        LogManager.appendMonitorLog("onSyncProgressChange $packageProgress / $packageTotalCount")
        for (listener in mMonitorEventListeners) {
            listener.onSyncProgressChange(packageNumber, packageProgress, packageTotalCount)
        }
    }

    override fun onSyncProgressChangeV2(packageNumber: Int, totalProgress: Int, totalCount: Int) {
        LogManager.appendMonitorLog("onSyncProgressChangeV2 $totalProgress / $totalCount")
        for (listener in mMonitorEventListeners) {
            listener.onSyncProgressChangeV2(packageNumber, totalProgress, totalCount)
        }
    }

    override fun onSyncSuccess() {
        mMonitorLiveData.value?.isSyncing = false
        notifyMonitorChange()
        if (AppManager.getBlueManager().isBluePeripheralConnected && AppUtils.isAppForeground()) {
            SyncPatternService.start(App.getAppContext())
        }
        for (listener in mMonitorEventListeners) {
            listener.onSyncSuccess()
        }
    }

    override fun onSyncFailed() {
        mMonitorLiveData.value?.isSyncing = false
        notifyMonitorChange()
        LogManager.appendTransparentLog("onSyncFailed")
        for (listener in mMonitorEventListeners) {
            listener.onSyncFailed()
        }
    }

    override fun onTurnOnPaModeStart() {
        mMonitorLiveData.value?.sleeperPaStatus = BlueDevice.PA_STATUS_TURNING_ON_PA
        for (listener in mMonitorEventListeners) {
            listener.onTurnOnPaModeStart()
        }
    }

    override fun onTurnOnPaModeSuccess() {
        for (listener in mMonitorEventListeners) {
            listener.onTurnOnPaModeSuccess()
        }
    }

    override fun onTurnOnPaModeFailed(message: String) {
        for (listener in mMonitorEventListeners) {
            listener.onTurnOnPaModeFailed(message)
        }
    }

    override fun onConnectStart() {
        if (mMonitorLiveData.value?.status != BlueDevice.STATUS_CONNECTING) {
            mMonitorLiveData.value?.status = BlueDevice.STATUS_CONNECTING
            notifyMonitorChange()
        }
        for (listener in mMonitorEventListeners) {
            listener.onConnectStart()
        }
    }

    override fun onConnectFailed() {
        mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
        notifyMonitorChange()
        for (listener in mMonitorEventListeners) {
            listener.onConnectFailed()
        }
    }

    override fun onConnectSuccess() {
        for (listener in mMonitorEventListeners) {
            listener.onConnectSuccess()
        }
    }

// ---------------- monitor event listener end ----------------

    private fun setMonitorToLiveData(monitor: BlueDevice?) {
        mMonitorLiveData.value = monitor
    }

    fun postIsUploadingSleepDataToServer(isUploading: Boolean) {
        mIsUploadingSleepDataToServerLiveData.postValue(isUploading)
    }

//    fun getAndCheckFirmVersion() {
//        mDeviceStateHelper.getAndCheckFirmVersion()
//    }
//
//    fun hasFirmwareNeedUpdate(): Boolean {
//        return mMonitorNeedUpdateLiveData.value == true || mSleeperNeedUpdateLiveData.value == true
//    }
}

interface MonitorEventListener {
    fun onSyncStart()

    /**
     * @param packageNumber   第几个数据包
     * @param packageProgress 当前包同步进度
     * @param packageTotalCount    当前包数据总量
     */
    fun onSyncProgressChange(packageNumber: Int, packageProgress: Int, packageTotalCount: Int)

    /**
     * @param packageNumber   第几个数据包
     * @param totalProgress 所有包同步进度
     * @param totalCount    所有包数据总量
     */
    fun onSyncProgressChangeV2(packageNumber: Int, totalProgress: Int, totalCount: Int)

    fun onSyncSuccess()

    fun onSyncFailed()

    fun onTurnOnPaModeStart()

    fun onTurnOnPaModeSuccess()

    fun onTurnOnPaModeFailed(message: String)

    fun onConnectStart()

    fun onConnectFailed()

    fun onConnectSuccess()
}
