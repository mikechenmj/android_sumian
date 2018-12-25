@file:Suppress("MemberVisibilityCanBePrivate")

package com.sumian.sd.device

import android.text.TextUtils
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.blue.callback.BlueAdapterCallback
import com.sumian.blue.callback.BluePeripheralCallback
import com.sumian.blue.callback.BluePeripheralDataCallback
import com.sumian.blue.constant.BlueConstant
import com.sumian.blue.model.BluePeripheral
import com.sumian.blue.model.bean.BlueUuidConfig
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.hw.log.LogManager
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.bean.BlueDevice
import com.sumian.sd.device.command.BlueCmd
import com.sumian.sd.device.pattern.SyncPatternService
import com.sumian.sd.device.wrapper.BlueDeviceWrapper
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.StorageUtil
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:42
 * desc   :
 * version: 1.0
 */
object DeviceManager : BlueAdapterCallback, BluePeripheralDataCallback, BluePeripheralCallback, MonitorEventListener {

    private const val SP_KEY_MONITOR_CACHE = "DeviceManager.MonitorCache"

    private var mCurrentIndex = -1
    private val m8fTransData = ArrayList<String>(0)
    private var mIsMonitoring: Boolean = false
    private var mIsUnbinding: Boolean = false
    private var mTranType: Int = 0
    private var mBeginCmd: String? = null
    private var mReceiveStartedTime: Long = 0
    private var mBlueDeviceWrapper: BlueDeviceWrapper = BlueDeviceWrapper()
    private var mTotalDataCount: Int = 0
    private var mPackageNumber: Int = 1 // 透传数据 包的index
    private val mMonitorLiveData = MutableLiveData<BlueDevice>()
    private val mIsBluetoothEnableLiveData = MutableLiveData<Boolean>()
    private val mMonitorEventListeners = HashSet<MonitorEventListener>()
    private val mIsUploadingSleepDataToServerLiveData = MutableLiveData<Boolean>()

    fun init() {
        AppManager.getBlueManager().addBlueAdapterCallback(this)
        val monitorCache = getCachedMonitor()
        setMonitorToLiveData(monitorCache)
        mIsBluetoothEnableLiveData.value = AppManager.getBlueManager().isEnable
        mIsUploadingSleepDataToServerLiveData.value = false
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

    fun getMonitorVersion(): String? {
        return mMonitorLiveData.value?.version
    }

    fun getSleeperVersion(): String? {
        return mMonitorLiveData.value?.sleeperVersion
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
        if (isConnected() || isConnecting() || !isBluetoothEnable()) {
            return
        }
        getCachedMonitor()?.let {
            scanAndConnect(it)
        }
    }

    fun scanAndConnect(monitor: BlueDevice) {
        monitor.status = BlueDevice.STATUS_CONNECTING
        setMonitorToLiveData(monitor)
        onConnectStart()
        mBlueDeviceWrapper.scan2Connect(monitor, { connect(monitor) }, {
            onConnectFailed()
        })
    }

    /**
     * 断开蓝牙后直接connect连不上，需要先扫描，再连接
     */
    private fun connect(monitor: BlueDevice) {
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
            bluePeripheral.addPeripheralDataCallback(this)
            bluePeripheral.addPeripheralCallback(this)
            bluePeripheral.connect()
            monitor.status = BlueDevice.STATUS_CONNECTING
            setMonitorToLiveData(monitor)
            LogManager.appendMonitorLog("主动连接监测仪  connect to   name=" + remoteDevice.name + "  address=" + remoteDevice.address)
        } else {
            LogManager.appendMonitorLog("主动连接监测仪  connect to  is invalid   because  init bluetoothDevice is null")
        }
    }

    fun turnOnSleeperPaMode() {
        val bluePeripheral = getCurrentBluePeripheral() ?: return
        bluePeripheral.writeDelay(BlueCmd.cDoSleepyPaMode(), 500)
        onTurnOnPaModeStart()
        LogManager.appendSpeedSleeperLog("主动 turn on  速眠仪 pa 模式")
    }

    fun syncSleepData() {
        if (isSyncing()) return
        val bluePeripheral = getCurrentBluePeripheral() ?: return
        if (mMonitorLiveData.value?.isSyncing == true) return
        bluePeripheral.write(BlueCmd.cSleepData())
        mPackageNumber = 1
        LogManager.appendTransparentLog("主动同步睡眠数据")
        mMonitorLiveData.value?.isSyncing = true
        notifyMonitorChange()
    }

    fun unbind() {
        setMonitorToLiveData(null)
        mIsUnbinding = true
        getCurrentBluePeripheral()?.close()
        clearCacheDevice()
        AppManager.getBlueManager().clearBluePeripheral()
        mBlueDeviceWrapper.release()
        LogManager.appendMonitorLog("主动解绑监测仪")
    }

    fun turnOnMonitoringMode(monitoringMode: Int) {
        val bluePeripheral = getCurrentBluePeripheral() ?: return
        bluePeripheral.write(BlueCmd.cDoMonitorMonitoringMode(monitoringMode))
    }

    fun isConnected(): Boolean {
        return mMonitorLiveData.value?.isConnected ?: false
    }

    fun isConnecting(): Boolean {
        return mMonitorLiveData.value?.status == BlueDevice.STATUS_CONNECTING
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
        mBlueDeviceWrapper.release()
        LogManager.appendBluetoothLog("蓝牙 turn off")
    }

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
            "40"//校正时区
            -> receiveSyncTimeSuccessCmd()
            "44"//获取监测仪电量
            -> receiveMonitorBatteryInfo(cmd)
            "45"//获取速眠仪电量
            -> receiveSleeperBatteryInfo(cmd)
            "4b" -> receiveSetUserInfoResult(cmd)
            "4e"//获取速眠仪的连接状态
            -> receiveSleeperConnectionStatus(peripheral, cmd)
            "4f"//主动获取睡眠特征数据
            -> receiveRequestSleepDataResponse(cmd)
            "50"//获取监测仪固件版本信息
            -> receiveMonitorVersionInfo(cmd)
            "59"//使速眠仪进入 dfu 模式开启成功
            -> receiveSleeperEnterDfuSuccessResponse(cmd)
            "51"//监测仪自己固件 dfu 模式开启成功
            -> receiveMonitorEnterDfuSuccessResponse(cmd)
            "52" -> LogManager.appendBluetoothLog("0x52 正在绑定速眠仪中,$cmd")
            "53"//获取监测仪的 sn 号
            -> receiveMonitorSnInfo(data, cmd)
            "54"//获取速眠仪的固件版本信息
            -> receiveSleeperVersionInfo(cmd)
            "55"//获取监测仪绑定的并且连接着的速眠仪的 sn 号
            -> receiveSleeperSnInfo(data, cmd)
            "56"//获取监测仪绑定的速眠仪的 mac 地址
            -> receiveSleeperMacInfo(cmd)
            "57"//开启/关闭监测仪的监测模式  0x01 开启  0x00 关闭
            -> receiveTurnOnOffMonitoringModeResponse(cmd)
            "58"//使速眠仪进入 pa 模式之后的反馈
            -> receiveSleeperEnterPaModeResponse(cmd)
            "61"//同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
            -> receiveAllMonitorAndSleeperStatus(peripheral, data, cmd)
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
            "8e" // 开始/结束 透传数据
            -> receiveStartOrFinishTransportCmd(peripheral, data, cmd)
            "8f" // 透传数据
            -> receiveSleepData(peripheral, data, cmd)
            "4a", "4c" -> {
            }
            else -> peripheral.write(BlueCmd.cResponseOk(data[1]))
        }
    }

    private fun receiveSleepData(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
        val indexOne = Integer.parseInt(cmd.substring(4, 6), 16) and 0x0f shl 8
        val indexTwo = Integer.parseInt(cmd.substring(6, 8), 16)
        val index = indexOne + indexTwo
        if (index % 20 == 0) {
            LogManager.appendTransparentLog("收到透传数据：index：$index, cmd: $cmd")
        }
        if (mCurrentIndex == -1) {
            mCurrentIndex = index
            peripheral.write(byteArrayOf(0xaa.toByte(), 0x8f.toByte(), 0x03, data[2], data[3], 0x88.toByte()))
            m8fTransData.add(cmd)
        } else {
            if (index == mCurrentIndex + 1) {
                mCurrentIndex = index
                peripheral.write(byteArrayOf(0xaa.toByte(), 0x8f.toByte(), 0x03, data[2], data[3], 0x88.toByte()))
                m8fTransData.add(cmd)
            } else if (index > mCurrentIndex + 1) {
                peripheral.write(byteArrayOf(0xaa.toByte(), 0x8f.toByte(), 0x03, data[2], data[3], 0xff.toByte()))
                LogManager.appendTransparentLog("index=$index  realCount=$mCurrentIndex  该index 出错,要求重传 cmd=$cmd")
            }
        }
        onSyncDataProgressChange(index, mTotalDataCount)
    }

    private fun receiveStartOrFinishTransportCmd(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
        val typeAndCount = Integer.parseInt(cmd.substring(4, 8), 16)
        //16 bit 包括4bit 类型 12bit 长度 向右移12位,得到高4位的透传数据类型
        val tranType = typeAndCount shr 12
        val dataCount: Int
        LogManager.appendFormatPhoneLog("receiveStartOrFinishTransportCmd: %d", data[4])
        when (data[4]) {
            0x01.toByte() //开始透传
            -> {
                mCurrentIndex = -1
                if (!m8fTransData.isEmpty()) {
                    m8fTransData.clear()
                }
                mTranType = tranType
                mBeginCmd = cmd
                mReceiveStartedTime = getActionTimeInSecond()
                mTotalDataCount = getDataCountFromCmd(cmd)
                dataCount = mTotalDataCount
                if (isAvailableStorageEnough(dataCount)) {
                    writeResponse(peripheral, data, true)
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,等待设备透传 " + dataCount + "包数据" + "  cmd=" + cmd)
                    onSyncStart()
                    AutoSyncDeviceDataUtil.saveAutoSyncTime()
                    mMonitorLiveData.value?.isSyncing = true
                } else {
                    writeResponse(peripheral, data, false)
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd)
                    onSyncFailed()
                    mMonitorLiveData.value?.isSyncing = false
                }
            }
            0x0f.toByte()// 结束。透传8f 数据接收完成,保存文件,准备上传数据到后台
            -> {
                mPackageNumber++
                dataCount = getDataCountFromCmd(cmd)
                @Suppress("UNCHECKED_CAST")
                if (dataCount == m8fTransData.size) {
                    val sleepData = m8fTransData.clone() as ArrayList<String>
                    m8fTransData.clear()
                    if (isAvailableStorageEnough(dataCount)) {
                        if (mTranType == 0x01) {
                            AutoSyncDeviceDataUtil.saveAutoSyncTime()
                        }
                        LogManager.appendMonitorLog("0x8e0f 透传数据" + dataCount + "包接收成功,准备写入本地文件 cmd=" + cmd)
                        postIsUploadingSleepDataToServer(true)
                        AppManager.getSleepDataUploadManager()
                                .saveSleepData(sleepData, mTranType, mBeginCmd, cmd,
                                        mMonitorLiveData.value?.sn,
                                        mMonitorLiveData.value?.sleeperSn,
                                        mReceiveStartedTime, getActionTimeInSecond())
                        writeResponse(peripheral, data, true)
                    } else {
                        writeResponse(peripheral, data, false)
                        LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd)
                    }
                } else {
                    LogManager.appendMonitorLog(
                            "0x8e0f 透传数据" + dataCount + "包接收失败,原因是包数量不一致 实际收到包数量 RealDataCount="
                                    + mCurrentIndex + " 重新透传数据已准备,等待设备重新透传  cmd=" + cmd)
                    mCurrentIndex = -1
                    this.m8fTransData.clear()
                    writeResponse(peripheral, data, false)
                }
                onSyncSuccess()
                mMonitorLiveData.value?.isSyncing = false
            }
            else -> {
            }
        }
        notifyMonitorChange()
    }

    private fun receiveAllMonitorAndSleeperStatus(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
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

    private fun receiveSleeperEnterPaModeResponse(cmd: String) {
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
            onTurnOnPaModeFailed(errorMessage ?: "未知错误")
        }
        mMonitorLiveData.value?.sleeperPaStatus = if (isPa) BlueDevice.PA_STATUS_PA else BlueDevice.PA_STATUS_NOT_PA
        notifyMonitorChange()
    }

    private fun receiveTurnOnOffMonitoringModeResponse(cmd: String) {
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

    private fun receiveSleeperMacInfo(cmd: String) {
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

    private fun onLastDeviceDataReceived() {
        saveCacheFile()
        turnOffMonitoringModeIfNeeded()
    }

    private fun turnOffMonitoringModeIfNeeded() {
        if (mMonitorLiveData.value?.isMonitoring == true) {
            turnOnMonitoringMode(BlueDevice.MONITORING_CMD_CLOSE)
        }
    }

    private fun uploadDeviceSns(monitorCache: BlueDevice?) {
        val map = HashMap<String, String>()
        val monitorSn = monitorCache?.sn
        monitorSn?.let { map.put("monitor_sn", it) }
        val sleeperSn = monitorCache?.sleeperSn
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

    private fun receiveSleeperSnInfo(data: ByteArray, cmd: String) {
        val sleepySn = BlueCmd.formatSn(data)
        mMonitorLiveData.value?.sleeperSn = sleepySn
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x55 获取到监测仪绑定的速眠仪的 sn=$sleepySn  cmd=$cmd")
    }

    private fun receiveSleeperVersionInfo(cmd: String) {
        val sleepyFirmwareVersion = getVersionFromCmd(cmd, 6)
        mMonitorLiveData.value?.sleeperVersion = sleepyFirmwareVersion
        if (cmd.length == 18) {
            mMonitorLiveData.value?.sleeperBomVersion = getVersionFromCmd(cmd, 12)
        }
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x54 速眠仪的固件版本信息$sleepyFirmwareVersion  cmd=$cmd")
    }

    private fun receiveMonitorSnInfo(data: ByteArray, cmd: String) {
        val monitorSn = BlueCmd.formatSn(data)
        LogManager.appendMonitorLog("0x53 获取到监测仪的sn=$monitorSn  cmd=$cmd")
        mMonitorLiveData.value?.sn = monitorSn
        notifyMonitorChange()
    }

    private fun receiveMonitorEnterDfuSuccessResponse(cmd: String) {
        mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
        mMonitorLiveData.value?.battery = 0
        mMonitorLiveData.value?.resetSleeper()
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x51 监测仪进入dfu 模式成功  cmd=$cmd")
    }

    private fun receiveSleeperEnterDfuSuccessResponse(cmd: String) {
        mMonitorLiveData.value?.status = BlueDevice.STATUS_UNCONNECTED
        mMonitorLiveData.value?.battery = 0
        mMonitorLiveData.value?.resetSleeper()
        notifyMonitorChange()
        LogManager.appendMonitorLog("0x59 速眠仪进入dfu 模式成功 cmd=$cmd")
    }

    private fun receiveMonitorVersionInfo(cmd: String) {
        // 55 50 03 【09 05 01】 老版本
        // 55 50 07 【01 00 02】 【0E】 【08 00 00】 新版本 【软件版本】【临床0C/正式0E】【bom版本号】
        val monitorFirmwareVersion = getVersionFromCmd(cmd, 6)
        mMonitorLiveData.value?.version = monitorFirmwareVersion
        if (cmd.length == 20) {
            mMonitorLiveData.value?.channelType = if (cmd.substring(12, 14).equals("0C")) BlueDevice.CHANNEL_TYPE_CLINIC else BlueDevice.CHANNEL_TYPE_NORMAL
            mMonitorLiveData.value?.bomVersion = getVersionFromCmd(cmd, 14)
        }
        notifyMonitorChange()
        LogManager.appendSpeedSleeperLog("0x50 监测仪的固件版本信息$monitorFirmwareVersion  cmd=$cmd")
    }

    private fun cmdToInt(cmd: String, startIndex: Int, endIndex: Int): Int {
        return Integer.parseInt(cmd.substring(startIndex, endIndex), 16)
    }

    private fun getVersionFromCmd(cmd: String, startIndex: Int): String {
        if (startIndex + 6 > cmd.length) {
            return ""
        } else {
            return "${cmdToInt(cmd, startIndex, startIndex + 2)}.${cmdToInt(cmd, startIndex + 2, startIndex + 4)}.${cmdToInt(cmd, startIndex + 4, startIndex + 6)}"
        }
    }

    private fun receiveRequestSleepDataResponse(cmd: String) {
        when (cmd) {
            "554f020188" -> {
                mMonitorLiveData.value?.isSyncing = true
                LogManager.appendTransparentLog("收到0x4f回复 发现设备有睡眠特征数据,准备同步中  cmd=$cmd")
            }
            "554f020100" -> {
                mMonitorLiveData.value?.isSyncing = false
                onSyncSuccess()
                LogManager.appendTransparentLog("收到0x4f回复 设备没有睡眠特征数据  cmd=$cmd")
                AutoSyncDeviceDataUtil.saveAutoSyncTime()
            }
            "554f0201ff" -> {
                onSyncFailed()
                mMonitorLiveData.value?.isSyncing = false
                LogManager.appendTransparentLog("收到0x4f回复 设备4f 指令识别异常  cmd=$cmd")
            }
            else -> {
            }
        }
        notifyMonitorChange()
    }

    private fun receiveSleeperConnectionStatus(peripheral: BluePeripheral, cmd: String) {
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

    private fun receiveSetUserInfoResult(cmd: String) {
        if ("554b0188" == cmd) {
            LogManager.appendMonitorLog("对设备设置 用户信息成功....$cmd")
        } else {
            LogManager.appendMonitorLog("对设备设置 用户信息失败...$cmd")
        }
    }

    private fun receiveSleeperBatteryInfo(cmd: String) {
        val sleepyBattery = Integer.parseInt(cmd.substring(cmd.length - 2), 16)
        mMonitorLiveData.value?.sleeperBattery = sleepyBattery
        notifyMonitorChange()
        LogManager.appendMonitorLog("0x44 收到速眠仪的电量变化---->$sleepyBattery  cmd=$cmd")
    }

    private fun receiveMonitorBatteryInfo(cmd: String) {
        val monitorBattery = Integer.parseInt(cmd.substring(cmd.length - 2), 16)
        mMonitorLiveData.value?.battery = monitorBattery
        notifyMonitorChange()
        LogManager.appendMonitorLog("0x45 收到监测仪的电量变化---->$monitorBattery  cmd=$cmd")
    }

    private fun receiveSyncTimeSuccessCmd() {
        LogManager.appendMonitorLog("收到0x40 监测仪校正时区成功")
    }

    private fun onSyncDataProgressChange(current: Int, total: Int) {
        onSyncProgressChange(mPackageNumber, current, total)
    }

    /**
     * @param peripheral       peripheral
     * @param data             data
     * @param readyForNextData true 准备接受, false 异常
     */
    private fun writeResponse(peripheral: BluePeripheral, data: ByteArray, readyForNextData: Boolean) {
        val command = byteArrayOf(0xaa.toByte(), 0x8e.toByte(), data[2], data[3], data[4], data[5], data[6], data[7], data[8], if (readyForNextData) 0x88.toByte() else 0xff.toByte())
        peripheral.write(command)
    }

    /**
     * dataCount + 2 表示加上了startCmd和endCmd
     * 每行命令有26个字符，加上换行，共27字符，一个字符占1byte，考虑安全性，这里计算时按30byte/cmd计算
     * 实测10000个cmd，文件273kb；
     */
    private fun isAvailableStorageEnough(dataCount: Int): Boolean {
        val dataBytes = (dataCount + 2) * 30L
        val availableExternalStorageSize = StorageUtil.getAvailableExternalStorageSize()
        return dataBytes < availableExternalStorageSize
    }

    /**
     * 获取该次透传数据总条数
     *
     * @return 透传数据总条数
     */
    private fun getDataCountFromCmd(cmd: String): Int {
        return Integer.parseInt(cmd.substring(5, 8), 16)
    }

    override fun onConnecting(peripheral: BluePeripheral, connectState: Int) {
        mMonitorLiveData.value?.status = BlueDevice.STATUS_CONNECTING
        mMonitorLiveData.value?.battery = 0
        mMonitorLiveData.value?.resetSleeper()
        notifyMonitorChange()
        LogManager.appendMonitorLog("监测仪正在连接中 " + peripheral.name)
    }

    override fun onConnectSuccess(peripheral: BluePeripheral, connectState: Int) {
        mMonitorLiveData.value?.status = BlueDevice.STATUS_CONNECTED
        notifyMonitorChange()
        onConnectSuccess()
        AppManager.getBlueManager().saveBluePeripheral(peripheral)
        LogManager.appendMonitorLog("监测仪连接成功 " + peripheral.name)
    }

    override fun onConnectFailed(peripheral: BluePeripheral, connectState: Int) {
        if (isSyncing()) {
            mMonitorLiveData.value?.isSyncing = false
            onSyncFailed()
        }
        onConnectFailed()
        AppManager.getBlueManager().refresh()
        LogManager.appendMonitorLog("监测仪连接失败 " + peripheral.name)
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
        LogManager.appendMonitorLog("连接成功,开始初始化同步监测仪与速眠仪相关状态数据 " + peripheral.name)
    }

    private fun getActionTimeInSecond(): Long {
        return System.currentTimeMillis() / 1000L
    }

    private fun saveCacheFile() {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, JsonUtil.toJson(mMonitorLiveData.value))
    }

    fun cacheBlueDevice(blueDevice: BlueDevice) {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, JsonUtil.toJson(blueDevice))
    }

    private fun clearCacheDevice() {
        SPUtils.getInstance().put(SP_KEY_MONITOR_CACHE, "")
        LogManager.appendUserOperationLog("设备被成功解绑,并清除掉缓存成功")
    }

    private fun getCurrentBluePeripheral(): BluePeripheral? {
        val bluePeripheral = AppManager.getBlueManager().bluePeripheral
        return if (bluePeripheral == null || !bluePeripheral.isConnected) {
            null
        } else bluePeripheral
    }

    private fun notifyMonitorChange() {
        // LiveData 调用set时会下发数据
        setMonitorToLiveData(mMonitorLiveData.value)
    }

    // ---------------- monitor event listener start ----------------

    override fun onSyncStart() {
        for (listener in mMonitorEventListeners) {
            listener.onSyncStart()
        }
    }

    override fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int) {
        for (listener in mMonitorEventListeners) {
            listener.onSyncProgressChange(packageNumber, progress, total)
        }
    }

    override fun onSyncSuccess() {
        if (AppManager.getBlueManager().isBluePeripheralConnected && AppUtils.isAppForeground()) {
            SyncPatternService.start(App.getAppContext())
        }
        for (listener in mMonitorEventListeners) {
            listener.onSyncSuccess()
        }
    }

    override fun onSyncFailed() {
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

    fun setIsUploadingSleepDataToServer(isUploading: Boolean) {
        mIsUploadingSleepDataToServerLiveData.value = isUploading
    }

    fun postIsUploadingSleepDataToServer(isUploading: Boolean) {
        mIsUploadingSleepDataToServerLiveData.postValue(isUploading)
    }

    fun getIsUploadingSleepDataToServerLiveData(): MutableLiveData<Boolean> {
        return mIsUploadingSleepDataToServerLiveData
    }

    fun uploadCacheSn() {
        val monitorCache = getCachedMonitor()
        uploadDeviceSns(monitorCache)
    }
}