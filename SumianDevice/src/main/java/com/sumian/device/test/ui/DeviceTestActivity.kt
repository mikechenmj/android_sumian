package com.sumian.device.test.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.clj.fastble.data.BleDevice
import com.clj.fastble.utils.HexUtil
import com.sumian.device.R
import com.sumian.device.authentication.AuthenticationManager
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.callback.ScanCallback
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.DeviceType
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.DfuCallback
import com.sumian.device.manager.upload.SleepDataUploadManager
import com.sumian.device.util.CmdConstans
import com.sumian.device.util.LogManager
import com.sumian.devicedemo.base.AdapterHost
import kotlinx.android.synthetic.main.layout_main_cmd.*
import kotlinx.android.synthetic.main.layout_main_device.*
import kotlinx.android.synthetic.main.layout_main_scan.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.sort


@SuppressLint("SetTextI18n")
class DeviceTestActivity : AppCompatActivity() {
    companion object {
        private const val REQ_CODE_GET_PERMISSION = 1000
        private const val SP_KEY_CONNECTED_DEVICE_ADDRESS = "SP_KEY_CONNECTED_DEVICE_ADDRESS"
        private const val SP_KEY_CONNECTED_DEVICE_NAME = "SP_KEY_CONNECTED_DEVICE_NAME"
    }

    private lateinit var mDeviceAdapter: DeviceAdapter
    private lateinit var mCmdHistoryAdapter: TextListAdapter
    private var mStartConnectTime = 0L
    private var mGatt: BluetoothGatt? = null
    private var mDevice: BleDevice? = null
    private var mMainHandler = Handler(Looper.getMainLooper())
    private var mProgressDialog: ProgressDialog? = null

    private val mDeviceAdapterHost = object :
            AdapterHost<BluetoothDevice> {
        override fun onItemClick(device: BluetoothDevice) {
            persistDevice(device)
            updateConnectedDeviceName(device.name)
            connectDevice(device.address)
        }
    }

    private val mCommonCmdAdapterHost = object : AdapterHost<CommonCmd> {
        override fun onItemClick(data: CommonCmd) {
            writeData(data.cmd)
        }
    }

    private val mScanCallback = object : ScanCallback {
        override fun onStart(success: Boolean) {
            progress_bar.isVisible = true
        }

        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
            LogManager.log("$device, ${device.name}")
            val name = device.name
            if (name.isNullOrEmpty() || !name.startsWith("M-SUMIAN")) return
            mDeviceAdapter.addData(device)
        }

        override fun onStop() {
            progress_bar.isVisible = false
            ToastUtils.showShort("scan finished")
        }
    }

    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String) {
            when (type) {
                // monitor
                DeviceManager.EVENT_RECEIVE_MONITOR_SN -> {
                    tv_monitor_sn.text = "mnt sn: ${DeviceManager.getDevice()?.monitorSn}"
                }
                DeviceManager.EVENT_RECEIVE_MONITOR_VERSION_INFO -> {
                    tv_monitor_sw_version.text =
                            "mnt sw version: ${DeviceManager.getDevice()?.monitorVersionInfo?.softwareVersion}"
                    tv_monitor_hw_version.text =
                            "mnt hw version: ${DeviceManager.getDevice()?.monitorVersionInfo?.hardwareVersion}"
                    tv_monitor_hl_version.text =
                            "mnt hl version: ${DeviceManager.getDevice()?.monitorVersionInfo?.heartBeatLibVersion}"
                    tv_monitor_sa_version.text =
                            "mnt sa version: ${DeviceManager.getDevice()?.monitorVersionInfo?.sleepAlgorithmVersion}"
                }
                DeviceManager.EVENT_MONITOR_CONNECT_STATUS_CHANGE -> {
                    tv_connect_status.text =
                            "mnt status: ${getConnectStatusString(DeviceManager.getDevice()?.monitorConnectStatus)}"
                }
                DeviceManager.EVENT_MONITOR_BATTERY_CHANGE -> {
                    tv_monitor_battery.text =
                            "mnt battery: ${DeviceManager.getDevice()?.monitorBattery}"
                }

                // sleep master
                DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_MAC -> {
                    tv_sm_address.text = "sm address: ${DeviceManager.getDevice()?.sleepMasterMac}"
                }
                DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_SN -> {
                    tv_sm_sn.text = "sm sn: ${DeviceManager.getDevice()?.sleepMasterSn}"
                }
                DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO -> {
                    tv_sm_sw_version.text =
                            "sm sw version: ${DeviceManager.getDevice()?.sleepMasterVersionInfo?.softwareVersion}"
                    tv_sm_hw_version.text =
                            "sm hw version: ${DeviceManager.getDevice()?.sleepMasterVersionInfo?.hardwareVersion}"
                    tv_sm_ha_version.text =
                            "sm ha version: ${DeviceManager.getDevice()?.sleepMasterVersionInfo?.headDetectAlgorithmVersion}"
                }
                DeviceManager.EVENT_SLEEP_MASTER_CONNECT_STATUS_CHANGE -> {
                    tv_sm_status.text =
                            "sm status: ${DeviceManager.getDevice()?.sleepMasterConnectStatus}"
                }
                DeviceManager.EVENT_SLEEP_MASTER_BATTERY_CHANGE -> {
                    tv_sm_battery.text =
                            "sm address: ${DeviceManager.getDevice()?.sleepMasterBattery}"
                }
                DeviceManager.EVENT_SLEEP_MASTER_WORK_MODE_CHANGE -> {
                    tv_sm_pa_on.text =
                            "sm address: ${DeviceManager.getDevice()?.isSleepMasterWorkModeOn()}"
                }

                // sync data
                DeviceManager.EVENT_SYNC_SLEEP_DATA_START,
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS,
                DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL
                -> {
                    tv_monitor_is_syncing.text =
                            "mnt is syncing: ${DeviceManager.getDevice()?.isSyncing}"
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE -> {
                    tv_monitor_sync_progress.text =
                            "mnt sync progress: ${DeviceManager.getDevice()?.syncProgress} / ${DeviceManager.getDevice()?.syncTotalCount}"
                }
            }
        }
    }

    private val mConnectCallback = object : ConnectDeviceCallback {
        override fun onStart() {
            mStartConnectTime = System.currentTimeMillis()
            showLoading()
        }

        override fun onSuccess() {
            mDevice = null
            dismissLoading()
        }

        override fun onFail(code: Int, msg: String) {
            dismissLoading()
//            LogManager.d(msg)
        }
    }

    private val mBleCommunicationWatcher = object : BleCommunicationWatcher {
        override fun onRead(
                data: ByteArray,
                hexString: String
        ) {
            addCmdToHistoryList(hexString, false)
        }

        override fun onWrite(
                data: ByteArray,
                hexString: String,
                success: Boolean,
                errorMsg: String?
        ) {
            if (TextUtils.isEmpty(errorMsg)) {
                addCmdToHistoryList(hexString, true)
            } else {
                addCmdToHistoryList("$hexString error: $errorMsg", true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_test)
        bt_scan.setOnClickListener { checkPermissionForScan() }
        bt_connect.setOnClickListener { connectOldDevice() }
        bt_disconnect.setOnClickListener { disconnectDevice() }
        bt_send.setOnClickListener { writeData(et_send.text.toString()) }
        initRvs()
        updateConnectedDeviceName(getPersistedDeviceName())
        initDeviceManager()
        bt_common_cmd.setOnClickListener { showCommonCmdPop() }
        iv_clear_cmd_history.setOnClickListener { mCmdHistoryAdapter.clear() }
        vg_cmd_history_label.setOnClickListener { switchVg(sw_show_cmd, vg_cmd) }
        vg_device_info_label.setOnClickListener { switchVg(sw_show_device_info, vg_device_info) }
        vg_scan_result_label.setOnClickListener { switchVg(sw_scan_result, vg_scan_result) }
        bt_sync_data.setOnClickListener { DeviceManager.startSyncSleepDataWithUi() }
        bt_write_mock_date.setOnClickListener {
            val cmd = "aa120303060b"
            writeAndSendCmd(cmd)
        }
        bt_login.setOnClickListener { AuthenticationManager.login() }
        bt_upload_data.setOnClickListener { SleepDataUploadManager.uploadNextTask() }
        bt_upgrade.setOnClickListener { upgradeMonitor() }
    }

    private fun writeAndSendCmd(cmd: String) {
        et_send.setText(cmd)
        writeData(et_send.text.toString())
    }

    private fun upgradeMonitor() {
        val dir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "dfu.zip")
        DeviceManager.upgrade(DeviceType.MONITOR, file.absolutePath, object : DfuCallback {
            override fun onStart() {
                LogManager.log("upgrade onStart")
            }

            override fun onProgressChange(progress: Int) {
                LogManager.log("upgrade progress: $progress / 100")
            }

            override fun onSuccess() {
                LogManager.log("upgrade onSuccess")
            }

            override fun onFail(code: Int, msg: String?) {
                LogManager.log("upgrade onFail: $msg")
            }
        })
    }

//    override fun onStart() {
//        super.onStart()
//        connectOldDevice()
//    }

    private fun switchVg(icon: View, vg: View) {
        vg.isVisible = !vg.isVisible
        icon.isSelected = !vg.isVisible
    }

    private fun initDeviceManager() {
        DeviceManager.registerBleCommunicationWatcher(mBleCommunicationWatcher)
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
    }

    override fun onDestroy() {
        DeviceManager.unregisterBleCommunicationWatcher(mBleCommunicationWatcher)
        DeviceManager.unregisterDeviceStatusListener(mDeviceStatusListener)
        super.onDestroy()
    }

    private fun initRvs() {
        mDeviceAdapter = DeviceAdapter(mDeviceAdapterHost)
        mCmdHistoryAdapter = TextListAdapter()
        initRv(rv_device, mDeviceAdapter)
        initRv(rv_cmd_history, mCmdHistoryAdapter)
    }

    private fun <T : RecyclerView.ViewHolder> initRv(
            rv: RecyclerView,
            adapter: RecyclerView.Adapter<T>
    ) {
        sort(listOf(1, 2, 3))
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = adapter
    }

    private fun writeData(s: String) {
        if (TextUtils.isEmpty(s)) {
            ToastUtils.showShort("empty data")
            return
        }
        if (!DeviceManager.isMonitorConnected()) {
            ToastUtils.showShort("device not connected")
            return
        }
        val data = if (cb_as_string.isChecked) HexUtil.hexStringToBytes(s) else s.toByteArray()
        DeviceManager.writeData(data)
    }

    private fun updateConnectedDeviceName(name: String?) {
        tv_monitor_address.text = "device: $name"
    }

    private fun connectOldDevice() {
        val address = getPersistedDeviceAddress()
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showShort("no device")
            return
        }
        checkPermissionAndConnect(address!!)
    }

    private fun persistDevice(bluetoothDevice: BluetoothDevice) {
        SPUtils.getInstance().put(SP_KEY_CONNECTED_DEVICE_NAME, bluetoothDevice.name)
        SPUtils.getInstance().put(SP_KEY_CONNECTED_DEVICE_ADDRESS, bluetoothDevice.address)
    }

    private fun getPersistedDeviceName(): String? {
        return SPUtils.getInstance().getString(SP_KEY_CONNECTED_DEVICE_NAME)
    }

    private fun getPersistedDeviceAddress(): String? {
        return SPUtils.getInstance().getString(SP_KEY_CONNECTED_DEVICE_ADDRESS)
    }

//    private fun updateConnectGattTv(status: Int) {
//        tv_connect_status.text = "status: \n${getConnectStatusString(status)}"
//        if (status == BluetoothProfile.STATE_CONNECTED) {
//            val connectTimeCost = System.currentTimeMillis() - mStartConnectTime
//            println("connectTimeCost: $connectTimeCost")
//            tv_connect_time_cost.text = "time cost:\n$connectTimeCost"
//        } else {
//            tv_connect_time_cost.text = "time cost:\n--"
//        }
//    }

    private fun getConnectStatusString(status: DeviceConnectStatus?): String {
        return when (status) {
            DeviceConnectStatus.DISCONNECTED -> "disconnected"
            DeviceConnectStatus.CONNECTING -> "connecting"
            DeviceConnectStatus.CONNECTED -> "connected"
            DeviceConnectStatus.DISCONNECTING -> "disconnecting"
            else -> "disconnected"
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions(
                    arrayOf(getLocationPermission(), Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQ_CODE_GET_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scan()
        } else {
            ToastUtils.showShort("no permission")
        }
    }

    private fun getLocationPermission() = Manifest.permission.ACCESS_FINE_LOCATION

    private fun checkPermissionForScan() {
        doItOrRequestPermission(Runnable { scan() })
    }

    private fun checkPermissionAndConnect(address: String) {
        doItOrRequestPermission(Runnable { connectDevice(address) })
    }

    private fun connectDevice(address: String) {
        DeviceManager.connectDevice(address, mConnectCallback)
    }

    private fun disconnectDevice() {
        DeviceManager.disconnect()
    }

    private fun doItOrRequestPermission(runnable: Runnable) {
        val hasPermission = hasPermission()
        if (hasPermission) {
            runnable.run()
        } else {
            requestPermission()
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkSelfPermission(getLocationPermission()) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_GET_PERMISSION) {
            checkPermissionForScan()
        }
    }

    private fun scan() {
        DeviceManager.scan(mScanCallback)
    }

    private fun addCmdToHistoryList(cmd: String, isWrite: Boolean) {
        val arrow = if (isWrite) "W:" else "R:"
        val time = SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault()).format(Date())
        val data = "$time $arrow $cmd"
        mCmdHistoryAdapter.addData(data)
        rv_cmd_history.smoothScrollToPosition(mCmdHistoryAdapter.itemCount - 1)
    }

    private fun showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.setMessage("loading")
            mProgressDialog?.show()
        }
    }

    private fun dismissLoading() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }

    private val mCommonCmdMap = CmdConstans.COMMON_CMD_MAP
    private val mCommonCmdKeys = mCommonCmdMap.keys.toList()

    private fun showCommonCmdPop() {
        val adapter =
                ArrayAdapter<String>(
                        this,
                        R.layout.list_item_common_cmd, mCommonCmdKeys
                )
        AlertDialog.Builder(this)
                .setTitle("Common cmd")
                .setAdapter(
                        adapter,
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                writeAndSendCmd(mCommonCmdMap[mCommonCmdKeys[which]] ?: return)
                            }
                        })
                .show()
    }

}