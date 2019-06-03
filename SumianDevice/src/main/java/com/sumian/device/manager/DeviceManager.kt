package com.sumian.device.manager

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.blankj.utilcode.util.SPUtils
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanner
import com.clj.fastble.utils.HexUtil
import com.sumian.device.authentication.AuthenticationManager
import com.sumian.device.callback.*
import com.sumian.device.data.*
import com.sumian.device.manager.blecommunicationcontroller.BleCommunicationController
import com.sumian.device.manager.helper.*
import com.sumian.device.manager.helper.DeviceStatusHelper.syncState
import com.sumian.device.manager.upload.SleepDataManager
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.LogManager
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference

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

    // bluetooth
    const val EVENT_BLUETOOTH_STATUS_CHANGE = "BluetoothStatusChange"

    const val SUMIAN_DEVICE_NAME_PREFIX = "M-SUMIAN"

    private const val SP_KEY_BOUND_DEVICE_ADDRESS = "sp_key_bound_device_address"
    const val WRITE_DATA_INTERVAL = 100L
    const val CONNECT_WRITE_INTERVAL = 100L

    var mMainHandler = Handler(Looper.getMainLooper())
    private var mSumianDevice: SumianDevice? = null

    lateinit var mApplication: Application

    private var mGatt: BluetoothGatt? = null
    private var mBleDevice: BleDevice? = null // 负责connect，disconnect
    private val mDataHandlerList = ArrayList<BleDataHandler>()
    private val mDeviceStatusListenerList = ArrayList<WeakReference<DeviceStatusListener>>()
    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(event: String) {
            val iterator = mDeviceStatusListenerList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val listener = next.get()
                if (listener != null) {
                    listener.onStatusChange(event)
                } else {
                    iterator.remove()
                }
            }
        }
    }

    fun init(application: Application, params: Params) {
        mApplication = application
        initBleManager(application)
        NetworkManager.init(params.baseUrl)
        registerBluetoothReceiver(application)
        SleepDataManager.init(application)
        BleCommunicationController.init()
        SyncSleepDataHelper.init()
        DeviceStatusHelper.init(application.applicationContext)
        initBoundDevice()
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
        for (listener in mBluetoothAdapterStateChangeListeners) {
            listener.onStateChange(on)
        }
        if (!on) {
            BleManager.getInstance().disconnectAllDevice()
            mSumianDevice = getBoundDevice()
            mGatt = null
            mBleDevice = null
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
                .setConnectOverTime(10000)
                .setOperateTimeout(5000)
    }

    fun isSupportBle(): Boolean {
        return BleManager.getInstance().isSupportBle
    }

    fun isBluetoothEnable(): Boolean {
        return BleManager.getInstance().isBlueEnable
    }

    fun enableBluetooth() {
        BleManager.getInstance().enableBluetooth()
    }

    fun disableBluetooth() {
        BleManager.getInstance().disableBluetooth()
    }

    fun scan(callback: ScanCallback) {
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                callback.onStop()
            }

            override fun onScanStarted(success: Boolean) {
                callback.onStart(success)
            }

            override fun onScanning(bleDevice: BleDevice?) {
                callback.onLeScan(bleDevice?.device ?: return, bleDevice.rssi, bleDevice.scanRecord)
            }
        })
    }

    fun stopScan() {
        BleScanner.getInstance().stopLeScan()
    }

    fun bind(address: String, callback: ConnectDeviceCallback?) {
        SPUtils.getInstance().put(SP_KEY_BOUND_DEVICE_ADDRESS, address)
        mSumianDevice = getBoundDevice()
        connectDevice(address, callback, false)
    }

    fun unbind() {
        SPUtils.getInstance().put(SP_KEY_BOUND_DEVICE_ADDRESS, "")
        disconnect()
        mSumianDevice = null
        postEvent(EVENT_MONITOR_UNBIND, null)
    }

    fun hasBoundDevice(): Boolean {
        return !TextUtils.isEmpty(getBoundDeviceAddress())
    }

    fun connectBoundDevice(callback: ConnectDeviceCallback? = null) {
        val boundDeviceAddress = getBoundDeviceAddress()
        if (TextUtils.isEmpty(boundDeviceAddress)) {
            return
        }
        connectDevice(boundDeviceAddress, callback, true)
    }

    private fun getBoundDeviceAddress() =
            SPUtils.getInstance().getString(SP_KEY_BOUND_DEVICE_ADDRESS, null)

    fun connectDevice(
            address: String,
            callback: ConnectDeviceCallback?,
            scanFirst: Boolean = false
    ) {
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
        scan(object : ScanCallback {
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

    private fun connectWithoutScan(
            address: String,
            callback: ConnectDeviceCallback?
    ) {
        BleManager.getInstance().connect(address, object : BleGattCallback() {
            override fun onStartConnect() {
                changeMonitorConnectStatus(DeviceConnectStatus.CONNECTING)
                callback?.onStart()
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
            }

            override fun onConnectSuccess(
                    bleDevice: BleDevice?,
                    gatt: BluetoothGatt?,
                    status: Int
            ) {
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
            }

            override fun onConnectFail(
                    bleDevice: BleDevice?,
                    exception: BleException?
            ) {
                changeMonitorConnectStatus(DeviceConnectStatus.DISCONNECTED)
                callback?.onFail(1, exception?.description ?: "设备连接失败")
            }
        })
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

    fun startSyncSleepData() {
        if (isMonitorConnected() && !isSyncingSleepData()) {
            SyncSleepDataHelper.startSyncSleepData()
        }
    }

    fun toggleSleeperWorkMode(on: Boolean, callback: AsyncCallback<Any?>) {
        DeviceStatusHelper.toggleSleepMasterWorkMode(on, callback)
    }

    fun isMonitorConnected(): Boolean {
        return mSumianDevice?.isMonitorConnected() ?: false
    }

    fun isSleepMasterConnected(): Boolean {
        return mSumianDevice?.isSleepMasterConnected() ?: false
    }

    fun registerDeviceStatusListener(listener: DeviceStatusListener) {
        mDeviceStatusListenerList.add(WeakReference(listener))
    }

    fun unregisterDeviceStatusListener(listener: DeviceStatusListener) {
        val iterator = mDeviceStatusListenerList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.get() == listener) {
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
        mDeviceStatusListener.onStatusChange(event)
    }

    fun makeRequestByCmd(cmd: String, callback: BleRequestCallback) {
        BleCommunicationController.requestByCmd(cmd, callback)
    }

    fun makeRequest(data: String, callback: BleRequestCallback) {
        BleCommunicationController.request(data, callback)
    }

    fun makeRequest(data: ByteArray, callback: BleRequestCallback) {
        BleCommunicationController.request(data, callback)
    }

    fun upgrade(target: DeviceType, filePath: String, callback: DfuCallback) {
        UpgradeDeviceHelper.upgrade(mApplication, target, filePath, callback)
    }

    fun getDevice(): SumianDevice? {
        return mSumianDevice
    }

    fun changeSleepMaster(sleepMasterSn: String?, callback: AsyncCallback<Any>) {
        DeviceStatusHelper.changeSleepMaster(sleepMasterSn, callback)
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

}