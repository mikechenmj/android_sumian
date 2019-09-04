package com.sumian.device.manager

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.blankj.utilcode.util.SPUtils
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.data.BleScanState
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.clj.fastble.utils.HexUtil
import com.sumian.device.R
import com.sumian.device.authentication.AuthenticationManager
import com.sumian.device.callback.*
import com.sumian.device.data.*
import com.sumian.device.dfu.DfuUpgradeHelper
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.manager.helper.*
import com.sumian.device.manager.helper.DeviceStateHelper.syncState
import com.sumian.device.manager.upload.SleepDataUploadManager
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.ILogger
import com.sumian.device.util.LogManager
import retrofit2.Call
import retrofit2.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 17:00
 * desc   : 设备管理门面类
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate")
object DeviceManager {
    // monitor
    const val EVENT_RECEIVE_MONITOR_VERSION_INFO = "ReceiveMonitorVersionInfo"
    const val EVENT_RECEIVE_MONITOR_SN = "ReceiveMonitorSn"
    const val EVENT_MONITOR_BATTERY_CHANGE = "MonitorBatteryChange"
    const val EVENT_MONITOR_CONNECT_STATUS_CHANGE = "MonitorConnectStatusChange"
    const val EVENT_MONITOR_UNBIND = "MonitorUnbind"

    // sleep master
    const val EVENT_RECEIVE_SLEEP_MASTER_MAC = "ReceiveSleepMasterMac"
    const val EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO = "ChangeSleepMasterVersionInfo"
    const val EVENT_RECEIVE_SLEEP_MASTER_SN = "ReceiveSleepMasterSn"

    const val EVENT_SLEEP_MASTER_BATTERY_CHANGE = "ReceiveSleepMasterBattery"
    const val EVENT_SLEEP_MASTER_CONNECT_STATUS_CHANGE = "SleepMasterConnectStatusChange"
    const val EVENT_SLEEP_MASTER_WORK_MODE_CHANGE = "SleepMasterWorkModeChange"

    // sync sleep data
    const val EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE = "SleepDataSyncProgressChange"
    const val EVENT_SYNC_SLEEP_DATA_START = "SleepDataSyncStart"
    const val EVENT_SYNC_SLEEP_DATA_FAIL = "SleepDataSyncFail"
    const val EVENT_SYNC_SLEEP_DATA_SUCCESS = "SleepDataSyncSuccess"
    const val EVENT_SYNC_SLEEP_DATA_PREPARE = "SleepDataSyncPrepare"
    const val EVENT_SYNC_SLEEP_DATA_AND_UPLOAD_FINISH = "SleepDataSyncAndUploadFinish"
    const val EVENT_ALL_SLEEP_DATA_UPLOADED = "AllSleepDataUploaded"

    // version compatibility
    const val PROTOCOL_VERSION_TO_HIGH = 1
    const val PROTOCOL_VERSION_COMPATIBLE = 0
    const val PROTOCOL_VERSION_TO_LOW = -1
    const val PROTOCOL_VERSION_INVALID = -2

    const val COMPAT_MONITOR_PROTOCOL_VERSION = 1
    const val COMPAT_SLEEP_MASTER_PROTOCOL_VERSION = 1

    // bluetooth
    const val EVENT_BLUETOOTH_STATUS_CHANGE = "BluetoothStatusChange"

    const val SUMIAN_DEVICE_NAME_PREFIX = "M-SUMIAN"

    private const val SP_KEY_BOUND_DEVICE_ADDRESS = "sp_key_bound_device_address"
    const val WRITE_DATA_INTERVAL = 200L
    const val CONNECT_WRITE_INTERVAL = 300L

    const val SCAN_DELAY = 1000L
    const val MESSAGE_SCAN_DEVICES = 10

    const val SCAN_TIMEOUT = 40000L

    var mMainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MESSAGE_SCAN_DEVICES -> {
                    if (isScanning()) {
                        if (!mHighPriorityScaning) {
                            stopScan()
                            mHighPriorityScaning = msg?.data.getBoolean("high_priority")
                            scan(msg.obj as ScanCallback)
                        }
                    } else {
                        mHighPriorityScaning = msg?.data.getBoolean("high_priority")
                        scan(msg.obj as ScanCallback)
                    }
                }
            }
        }
    }

    private var mSumianDevice: SumianDevice? = null

    lateinit var mApplication: Application

    private var mBluetoothEnabled = false
    private var mHighPriorityScaning = false
    private var mGatt: BluetoothGatt? = null
    private var mBleDevice: BleDevice? = null // 负责connect，disconnect
    private val mDataHandlerList = ArrayList<BleDataHandler>()
    private val mDeviceStatusListenerList = ArrayList<DeviceStatusListener>()
    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(event: String, data: Any?) {
            var copyList: ArrayList<DeviceStatusListener> = ArrayList(mDeviceStatusListenerList)
            for (listener in copyList) {
                listener.onStatusChange(event, data)
            }
        }
    }

    fun init(application: Application, params: Params) {
        mApplication = application
        initBleManager(application)
        NetworkManager.init(params.baseUrl)
        registerBluetoothReceiver(application)
        SleepDataUploadManager.init(application)
        BleCommunicationController.init()
        SyncSleepDataHelper.init()
        DeviceStateHelper.init(application.applicationContext)
        initBoundDevice()
        mBluetoothEnabled = BleManager.getInstance().isBlueEnable
    }

    private fun registerBluetoothReceiver(context: Context) {
        val blueReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                        BluetoothAdapter.STATE_ON -> {
                            onBluetoothStateChange(true)
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            onBluetoothStateChange(false)
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(blueReceiver, filter)
    }

    private fun onBluetoothStateChange(on: Boolean) {
        mBluetoothEnabled = on
        for (listener in mBluetoothAdapterStateChangeListeners) {
            listener.onStateChange(on)
        }
        if (!on) {
            stopScan()
            BleManager.getInstance().disconnectAllDevice()
            BleManager.getInstance().destroy()
            mSumianDevice = getBoundDevice()
            mGatt = null
            mBleDevice = null
        } else {
            connectBoundDevice()
        }
        postEvent(EVENT_BLUETOOTH_STATUS_CHANGE)
    }

    private fun initBoundDevice() {
        mSumianDevice = getBoundDevice()
    }

    private fun getBoundDevice(): SumianDevice? {
        val boundDeviceAddress = getBoundDeviceAddress()
        var device: SumianDevice? = null
        if (!TextUtils.isEmpty(boundDeviceAddress)) {
            device = SumianDevice.createByAddress(boundDeviceAddress)
        }
        return device
    }

    private fun initBleManager(application: Application) {
        BleManager.getInstance().init(application)
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(3, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000)

        var scanRuleConfig = BleScanRuleConfig.Builder()
                .setScanTimeOut(SCAN_TIMEOUT)
                .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
    }

    fun isSupportBle(): Boolean {
        return BleManager.getInstance().isSupportBle
    }

    fun isBluetoothEnable(): Boolean {
        return mBluetoothEnabled
    }

    fun enableBluetooth() {
        BleManager.getInstance().enableBluetooth()
    }

    fun disableBluetooth() {
        BleManager.getInstance().disableBluetooth()
    }

    fun scanDelay(callback: ScanCallback, delay: Long = SCAN_DELAY, highPriority: Boolean = false) {
        var message = Message.obtain()
        message.what = MESSAGE_SCAN_DEVICES
        message.obj = callback
        message.data = Bundle().let {
            it.putBoolean("high_priority", highPriority)
            it
        }
        mMainHandler.sendMessageDelayed(message, delay)
    }

    fun removeScanMessage() {
        mMainHandler.removeMessages(MESSAGE_SCAN_DEVICES)
    }

    fun scan(callback: ScanCallback) {
        BleManager.getInstance().scan(object : BleScanCallback() {

            private var mFound = false

            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                LogManager.bleScanLog("onScanFinished: ${scanResultList?.size}")
                callback.onStop()
                if (!mFound) {
                    LogManager.bleScanLog("未扫到对应蓝牙设备")
                }
                mHighPriorityScaning = false
            }

            override fun onScanStarted(success: Boolean) {
                callback.onStart(success)
            }

            override fun onScanning(bleDevice: BleDevice?) {
                if (getBoundDeviceAddress() != null && getBoundDeviceAddress() == bleDevice?.device?.address) {
                    mFound = true
                }
                if (bleDevice?.device?.name != null && bleDevice?.device?.name != null
                        && bleDevice.device.name.startsWith("M-SUMIAN")) {
                    LogManager.bleScanLog("扫到速眠蓝牙设备: ${bleDevice?.device?.name}")
                }
                callback.onLeScan(bleDevice?.device ?: return, bleDevice.rssi, bleDevice.scanRecord)
            }
        })
    }

    fun stopScan() {
        removeScanMessage()
        if (isScanning()) {
            BleManager.getInstance().cancelScan()
        }
    }

    fun isScanning(): Boolean {
        return BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING
    }

    fun bind(address: String, callback: ConnectDeviceCallback?) {
        SPUtils.getInstance().put(SP_KEY_BOUND_DEVICE_ADDRESS, address)
        mSumianDevice = getBoundDevice()
        connectDevice(address, callback, false)
    }

    fun unbind() {
        SPUtils.getInstance().put(SP_KEY_BOUND_DEVICE_ADDRESS, "", true)
        disconnect()
        mSumianDevice = null
        postEvent(EVENT_MONITOR_UNBIND, null)
    }

    fun hasBoundDevice(): Boolean {
        return !TextUtils.isEmpty(getBoundDeviceAddress())
    }

    private var logConnectDeviceCallback = object : ConnectDeviceCallback {
        override fun onStart() {
            LogManager.bleScanLog("自动连接蓝牙开始")
        }

        override fun onSuccess() {
            LogManager.bleScanLog("自动连接蓝牙成功")
        }

        override fun onFail(code: Int, msg: String) {
            LogManager.bleScanLog("自动连接蓝牙失败: $code")
        }
    }

    fun connectBoundDevice(callback: ConnectDeviceCallback? = logConnectDeviceCallback) {
        val boundDeviceAddress = getBoundDeviceAddress()
        if (TextUtils.isEmpty(boundDeviceAddress)) {
            return
        }
        connectDevice(boundDeviceAddress, callback, true)
    }

    private fun getBoundDeviceAddress() =
            SPUtils.getInstance().getString(SP_KEY_BOUND_DEVICE_ADDRESS, null)

    /**
     * @param scanFirst 是否 先扫描再连接，大大减少聊不上的概率
     */
    fun connectDevice(
            address: String,
            callback: ConnectDeviceCallback?,
            scanFirst: Boolean = false
    ) {
        if (mSumianDevice?.monitorConnectStatus == DeviceConnectStatus.CONNECTING
                || mSumianDevice?.monitorConnectStatus == DeviceConnectStatus.CONNECTED) {
            return
        }
        disconnect()
        if (scanFirst) {
            scanAndConnect(address, callback)
        } else {
            connectWithoutScan(address, callback)
        }
    }

    private fun scanAndConnect(address: String, callback: ConnectDeviceCallback?) {
        callback?.onStart()
        changeMonitorConnectStatus(DeviceConnectStatus.CONNECTING)

        var device = BleManager.getInstance().bluetoothAdapter?.getRemoteDevice(address)

        if (device != null && device.name != null && !device.name.isEmpty()) {
            connectWithoutScan(address, callback)
            return
        }

        scanDelay(object : ScanCallback {
            private var mFound = false

            override fun onStart(success: Boolean) {
            }

            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                if (address == device.address) {
                    mFound = true
                    connectWithoutScan(address, callback)
                    stopScan()
                }
            }

            override fun onStop() {
                if (!mFound) {
                    changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTED)
                    callback?.onFail(1, "设备连接失败")
                }
            }
        })
    }

    private var mConnectMark = 0L
    private fun connectWithoutScan(
            address: String,
            callback: ConnectDeviceCallback?
    ) {
        mGatt?.close()
        var tempConnectMark = System.currentTimeMillis()
        mConnectMark = tempConnectMark
        var tempGatt = BleManager.getInstance().connect(address, object : BleGattCallback() {
            override fun onStartConnect() {
                changeMonitorConnectStatus(DeviceConnectStatus.CONNECTING)
                callback?.onStart()
                LogManager.bleConnectLog("连接开始")
            }

            override fun onDisConnected(
                    isActiveDisConnected: Boolean,
                    device: BleDevice?,
                    gatt: BluetoothGatt?,
                    status: Int
            ) {
                mGatt?.close()
                mBleDevice = null
                mGatt = null
                changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTED)
                LogManager.bleConnectLog("${device?.name}设备断开连接 and status: $status ")
            }

            override fun onConnectSuccess(
                    bleDevice: BleDevice?,
                    gatt: BluetoothGatt?,
                    status: Int
            ) {
                if (!SPUtils.getInstance().getString(SP_KEY_BOUND_DEVICE_ADDRESS).isNullOrEmpty()) {
                    mBleDevice = bleDevice
                    mGatt = gatt
                    mSumianDevice = SumianDevice.createByAddress(bleDevice?.mac!!)
                    changeMonitorConnectStatus(DeviceConnectStatus.CONNECTED)
                    callback?.onSuccess()
                    mMainHandler.postDelayed(
                            {
                                BleCommunicationController.startListenDeviceNotification(mBleDevice)
                                syncState()
                            }, CONNECT_WRITE_INTERVAL
                    )
                    LogManager.bleConnectLog("${bleDevice?.name}设备连接成功 and status: $status")
                } else {
                    BleManager.getInstance().disconnect(bleDevice)
                    changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTING)
                    LogManager.bleConnectLog("${bleDevice?.name}设备连接成功 and status: $status，但是用户手动解绑，因此断开连接")
                }
            }

            override fun onConnectFail(
                    bleDevice: BleDevice?,
                    exception: BleException?
            ) {
                changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTED)
                callback?.onFail(1, "连接失败,请确保设备在手机附近并重试")
                LogManager.bleConnectLog(exception?.description ?: "设备连接失败")
            }
        })

        mGatt = tempGatt
        mMainHandler.postDelayed({
            if (tempConnectMark == mConnectMark && tempGatt == mGatt) {
                if (mBleDevice == null && tempGatt != null && tempGatt?.services != null
                        && tempGatt?.services.size > 0) {
                    tempGatt?.discoverServices()
                }
            }
        }, SCAN_TIMEOUT + 200)

    }

    private fun changeMonitorConnectStatus(status: DeviceConnectStatus) {
        if (mSumianDevice != null) {
            mSumianDevice?.monitorConnectStatus = status
        }
        postEvent(EVENT_MONITOR_CONNECT_STATUS_CHANGE)
    }

    fun writeData(data: ByteArray, delay: Long = 0, callback: WriteBleDataCallback? = null) {
        BleCommunicationController.writeData(data, delay, callback)
    }

    fun writeData(data: String, delay: Long = 0, callback: WriteBleDataCallback? = null) {
        BleCommunicationController.writeData(HexUtil.hexStringToBytes(data), delay, callback)
    }

    fun disconnect() {
        BleManager.getInstance().disconnect(mBleDevice)
        changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTING)
    }

    private fun isMonitorVersionCompat() = checkMonitorVersionCompatibility() == PROTOCOL_VERSION_COMPATIBLE
    private fun isSleepMasterVersionCompat() = checkSleepMasterVersionCompatibility() == PROTOCOL_VERSION_COMPATIBLE
    fun isDeviceVersionCompatForSyncingData() = isMonitorVersionCompat() && (!isSleepMasterConnected() || isSleepMasterVersionCompat())

    fun startSyncSleepData(retry: Boolean = true): SyncSleepDataHelper.SyncState {
        var state: SyncSleepDataHelper.SyncState?
        if (retry) {
            state = startSyncSleepDataWithRetry()
        } else {
            state = startSyncSleepDataWithState()
            if (state == SyncSleepDataHelper.SyncState.FAIL_VERSION_WRONG) {
                postEvent(EVENT_SYNC_SLEEP_DATA_FAIL, null)
            }
        }
        if (state == SyncSleepDataHelper.SyncState.FAIL_IS_SYNCING) {
            if (!SyncSleepDataHelper.isSleepDataTypeSyncing()) {
                postEvent(EVENT_SYNC_SLEEP_DATA_SUCCESS, null)
            }
        }
        return state
    }

    private fun queryMonitorVersionInNeed(onNext: () -> Unit) {
        if (!isMonitorVersionCompat()) {
            DeviceStateHelper.queryMonitorVersion()
            LogManager.bleFlowLog("因手环版本不兼容需要重试")
            registerDeviceStatusListener(object : DeviceStatusListener {
                override fun onStatusChange(type: String) {
                    if (type == EVENT_RECEIVE_MONITOR_VERSION_INFO) {
                        LogManager.bleFlowLog("请求手环版本成功")
                        onNext()
                        unregisterDeviceStatusListener(this)
                    }
                }
            })
        } else {
            onNext()
        }
    }

    private fun querySleepMasterVersionInNeed(onNext: () -> Unit) {
        if (isSleepMasterConnected() && !isSleepMasterVersionCompat()) {
            DeviceStateHelper.querySleepMasterVersion()
            LogManager.bleFlowLog("因速眠仪版本不兼容需要重试")
            registerDeviceStatusListener(object : DeviceStatusListener {
                override fun onStatusChange(type: String) {
                    if (type == EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO) {
                        LogManager.bleFlowLog("请求速眠仪版本成功")
                        onNext()
                        unregisterDeviceStatusListener(this)
                    }
                }
            })
        } else {
            onNext()
        }
    }

    private fun startSyncSleepDataWithRetry(): SyncSleepDataHelper.SyncState {
        var state = startSyncSleepDataWithState()
        if (state == SyncSleepDataHelper.SyncState.FAIL_VERSION_WRONG) {
            var startSyncSleepDataWithState = {
                if (isMonitorConnected()) {
                    var retryState = startSyncSleepDataWithState()
                    if (retryState == SyncSleepDataHelper.SyncState.FAIL_VERSION_WRONG) {
                        postEvent(EVENT_SYNC_SLEEP_DATA_FAIL, null)
                    }
                } else {
                    postEvent(EVENT_SYNC_SLEEP_DATA_FAIL, null)
                }
            }
            queryMonitorVersionInNeed { querySleepMasterVersionInNeed { startSyncSleepDataWithState() } }
            return SyncSleepDataHelper.SyncState.RETRY
        }
        return state
    }

    private fun startSyncSleepDataWithState(): SyncSleepDataHelper.SyncState {
        return if (isDeviceVersionCompatForSyncingData()) {
            var start = startSyncSleepDataInternal()
            if (start) {
                SyncSleepDataHelper.SyncState.START
            } else {
                LogManager.bleFlowLog(mApplication.getString(R.string.sync_fail_is_syncing_tip))
                SyncSleepDataHelper.SyncState.FAIL_IS_SYNCING
            }
        } else {
            LogManager.bleFlowLog(mApplication.getString(R.string.sync_fail_connect_or_version_wrong_tip))
            LogManager.bleFlowLog("isMonitorVersionCompat(): ${isMonitorVersionCompat()}" +
                    ": ${mSumianDevice?.monitorVersionInfo?.protocolVersion}")
            LogManager.bleFlowLog("isSleepMasterConnected(): ${isSleepMasterConnected()}" +
                    ": ${mSumianDevice?.sleepMasterConnectStatus}")
            LogManager.bleFlowLog("isSleepMasterVersionCompat(): ${isSleepMasterVersionCompat()}" +
                    ": ${mSumianDevice?.sleepMasterVersionInfo?.protocolVersion}")
            SyncSleepDataHelper.SyncState.FAIL_VERSION_WRONG
        }
    }

    private fun startSyncSleepDataInternal(): Boolean {
        return SyncSleepDataHelper.startSyncSleepData()
    }

    fun toggleSleeperWorkMode(on: Boolean, callback: AsyncCallback<Any?>) {
        DeviceStateHelper.toggleSleepMasterWorkMode(on, callback)
    }

    fun isMonitorConnected(): Boolean {
        return mSumianDevice?.isMonitorConnected() ?: false
    }

    fun isSleepMasterConnected(): Boolean {
        return mSumianDevice?.isSleepMasterConnected() ?: false
    }

    fun registerDeviceStatusListener(listener: DeviceStatusListener) {
        mDeviceStatusListenerList.add(listener)
    }

    fun unregisterDeviceStatusListener(listener: DeviceStatusListener) {
        val iterator = mDeviceStatusListenerList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == listener) {
                iterator.remove()
            }
        }
    }

    fun registerBleCommunicationWatcher(watcher: BleCommunicationWatcher) {
        BleCommunicationController.registerBleCommunicationWatcher(watcher)
    }

    fun unregisterBleCommunicationWatcher(watcher: BleCommunicationWatcher) {
        BleCommunicationController.unregisterBleCommunicationWatcher(watcher)
    }

    fun isSyncingSleepData(): Boolean {
        return SyncSleepDataHelper.isSyncing()
    }

    fun getSyncSleepDataProgress(): Int {
        return SyncSleepDataHelper.getSyncSleepDataProgress()
    }

    fun getSyncSleepDataTotalCount(): Int {
        return SyncSleepDataHelper.getSyncSleepDataTotalCount()
    }

    fun postEvent(event: String, data: Any? = null) {
        LogManager.log(event, data?.toString())
        when (event) {
            EVENT_MONITOR_BATTERY_CHANGE -> {
                mSumianDevice?.monitorBattery = data as Int
            }
            EVENT_RECEIVE_MONITOR_VERSION_INFO -> {
                mSumianDevice?.monitorVersionInfo = data as MonitorVersionInfo
            }
            EVENT_RECEIVE_MONITOR_SN -> {
                mSumianDevice?.monitorSn = data as String
            }
            EVENT_SLEEP_MASTER_BATTERY_CHANGE -> {
                mSumianDevice?.sleepMasterBattery = data as Int
            }
            EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO -> {
                mSumianDevice?.sleepMasterVersionInfo = data as SleepMasterVersionInfo
            }
            EVENT_RECEIVE_SLEEP_MASTER_SN -> {
                mSumianDevice?.sleepMasterSn = data as String
            }
            EVENT_RECEIVE_SLEEP_MASTER_MAC -> {
                mSumianDevice?.sleepMasterMac = data as String
            }
            EVENT_SLEEP_MASTER_CONNECT_STATUS_CHANGE -> {
                mSumianDevice?.sleepMasterConnectStatus = data as DeviceConnectStatus
            }
            EVENT_SLEEP_MASTER_WORK_MODE_CHANGE -> {
                mSumianDevice?.sleepMasterWorkModeStatus =
                        if (data as Boolean) SleepMasterWorkModeStatus.ON else SleepMasterWorkModeStatus.OFF
            }
            EVENT_SYNC_SLEEP_DATA_START -> {
                mSumianDevice?.isSyncing = true
            }
            EVENT_SYNC_SLEEP_DATA_SUCCESS -> {
                mSumianDevice?.isSyncing = false
            }
            EVENT_SYNC_SLEEP_DATA_FAIL -> {
                mSumianDevice?.isSyncing = false
            }
            EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE -> {
                val progress = (data as IntArray)
                mSumianDevice?.syncProgress = progress[0]
                mSumianDevice?.syncTotalCount = progress[1]
            }
        }
        mDeviceStatusListener.onStatusChange(event, data)
    }

    fun makeRequest(data: ByteArray, callback: BleRequestCallback) {
        BleCommunicationController.requestWithRetry(data, callback)
    }

    fun upgradeBoundDevice(target: DeviceType, filePath: String,
                           onStart: () -> Unit,
                           onProgressChange: (progress: Int) -> Unit,
                           onSuccess: () -> Unit,
                           onFail: (code: Int, msg: String?) -> Unit) {
        DfuUpgradeHelper.upgradeBoundDevice(target, filePath, onStart, onProgressChange, onSuccess, onFail)
    }

    fun getDevice(): SumianDevice? {
        return mSumianDevice
    }

    fun changeSleepMaster(sleepMasterSn: String?, callback: AsyncCallback<Any>) {
        DeviceStateHelper.changeSleepMaster(sleepMasterSn, callback)
    }

    fun getLatestVersionInfo(callback: AsyncCallback<DeviceVersionInfo>) {
        NetworkManager.getApi()
                .getDeviceLatestVersionInfo(
                        mSumianDevice?.monitorVersionInfo?.hardwareVersion,
                        mSumianDevice?.sleepMasterVersionInfo?.hardwareVersion
                ).enqueue(object : retrofit2.Callback<DeviceVersionInfo> {
                    override fun onFailure(call: Call<DeviceVersionInfo>, t: Throwable) {
                        callback.onFail(1, t.message ?: "error known")
                    }

                    override fun onResponse(
                            call: Call<DeviceVersionInfo>,
                            response: Response<DeviceVersionInfo>
                    ) {
                        if (response.isSuccessful) {
                            callback.onSuccess(response.body())
                        } else {
                            callback.onFail(response.code(), response.message())
                        }
                    }
                })
    }

    fun syncPattern() {
        SyncPatternHelper.syncPattern()
    }

    data class Params(var baseUrl: String, var isDebug: Boolean = false)

    fun getMonitorSoftwareVersion(): String? {
        return mSumianDevice?.monitorVersionInfo?.softwareVersion
    }

    fun getSleepMasterSoftwareVersion(): String? {
        return mSumianDevice?.sleepMasterVersionInfo?.softwareVersion
    }

    fun setSleepMasterSn(sn: String) {
        mSumianDevice?.sleepMasterSn = sn
    }

    interface BluetoothAdapterStateChangeListener {
        fun onStateChange(on: Boolean)
    }

    private val mBluetoothAdapterStateChangeListeners = ArrayList<BluetoothAdapterStateChangeListener>()

    fun registerBluetoothAdapterStateChangeListener(listener: BluetoothAdapterStateChangeListener) {
        if (!mBluetoothAdapterStateChangeListeners.contains(listener)) {
            mBluetoothAdapterStateChangeListeners.add(listener)
        }
    }

    fun unregisterBluetoothAdapterStateChangeListener(listener: BluetoothAdapterStateChangeListener) {
        mBluetoothAdapterStateChangeListeners.remove(listener)
    }

    fun setToken(token: String?) {
        AuthenticationManager.mToken = token
    }

    fun checkMonitorVersionCompatibility(): Int {
        return checkDeviceVersionCompatibility(mSumianDevice?.monitorVersionInfo?.protocolVersion, COMPAT_MONITOR_PROTOCOL_VERSION)
    }

    fun checkSleepMasterVersionCompatibility(): Int {
        return checkDeviceVersionCompatibility(mSumianDevice?.sleepMasterVersionInfo?.protocolVersion, COMPAT_SLEEP_MASTER_PROTOCOL_VERSION)
    }

    private fun checkDeviceVersionCompatibility(deviceVersion: Int?, compatVersion: Int): Int {
        if (deviceVersion == null) {
            return PROTOCOL_VERSION_INVALID
        }
        return when {
            deviceVersion == compatVersion -> PROTOCOL_VERSION_COMPATIBLE
            deviceVersion < compatVersion -> PROTOCOL_VERSION_TO_LOW
            else -> PROTOCOL_VERSION_TO_HIGH
        }
    }

    fun setLogger(logger: ILogger) {
        LogManager.setLogger(logger)
    }

    fun clearPendingUploadSleepDataFile() {
        SleepDataUploadManager.clearAllTask()
    }

    interface DeviceResultCallback {
        fun onResult()
    }
}