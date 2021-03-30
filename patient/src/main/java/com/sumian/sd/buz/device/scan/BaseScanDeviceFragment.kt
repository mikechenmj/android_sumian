package com.sumian.sd.buz.device.scan

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.utils.LocationManagerUtil
import com.sumian.common.utils.PermissionUtil
import com.sumian.device.callback.ScanCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.utils.BluetoothUtil
import java.util.*

abstract class BaseScanDeviceFragment : BaseFragment() {

    protected var mScanning: Boolean = false
    protected var mFragmentDestroy = false

    companion object {
        const val REQUEST_CODE_FUNCTION_BT = 1
        const val REQUEST_CODE_FUNCTION_LOCATION = 2
        const val REQUEST_CODE_PERMISSION_DETAIL = 3
        const val REQUEST_PERMISSION_LOCATION_AND_STORAGE = 3
        const val MONITOR_NAME = "M-SUMIAN"
        const val MONITOR_NAME_PRO = "M-SUMIAN-PRO"
        const val SLEEP_MASTER_NAME = "PDNWK"
    }

    private val mScanDeviceCallback = object : ScanCallback {
        override fun onStart(success: Boolean) {
            mScanning = true
            onScanStart(success)
        }

        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
            val blueDevice = BlueDevice()
            blueDevice.name = device.name ?: return
            blueDevice.mac = device.address
            blueDevice.rssi = rssi
            val isDeviceVersionValid = isLeScanDeviceVersionValid(blueDevice, scanRecord)
            LogManager.appendBluetoothLog(
                    String.format(Locale.getDefault(),
                            "${javaClass.simpleName} 搜索到 %s %s, isVersionValid: %b",
                            device.name,
                            device.address,
                            isDeviceVersionValid))
            this@BaseScanDeviceFragment.onLeScan(device, blueDevice, isDeviceVersionValid)
        }

        override fun onStop() {
            mScanning = false
            onScanStop()
        }
    }

    protected fun startRequestBleEnable() {
        SdLogManager.logPermission("startRequestBleEnable")
        BluetoothUtil.startActivityForOpenBluetooth(this, REQUEST_CODE_FUNCTION_BT)
    }

    protected fun startScan() {
        DeviceManager.scanDelay(mScanDeviceCallback, highPriority = true)
    }

    protected fun stopScan() {
        DeviceManager.stopScan()
    }

    protected fun isBluetoothEnable() = DeviceManager.isBluetoothEnable()

    protected fun isBluetoothEnableAndHasPermissions() = isBluetoothEnable() && hasBluetoothPermissions()

    protected fun hasBluetoothPermissions(): Boolean {
        return PermissionUtil.hasBluetoothPermissions(activity!!)
    }

    protected fun checkLocationService(): Boolean {
        return LocationManagerUtil.checkLocationService(this, REQUEST_CODE_FUNCTION_LOCATION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DeviceManager.registerBluetoothAdapterStateChangeListener(mBluetoothStateChangeListener)
        requestPermissionOrStartScan()
    }

    override fun onDestroyView() {
        DeviceManager.unregisterBluetoothAdapterStateChangeListener(mBluetoothStateChangeListener)
        mFragmentDestroy = true
        stopScan()
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (activity != null) {
            if (requestCode === REQUEST_PERMISSION_LOCATION_AND_STORAGE) {
                var allGranted = true
                for (i in permissions.indices) {
                    SdLogManager.logPermission("requestPermissionOrStartScan permission: ${ActivityCompat.checkSelfPermission(activity!!, permissions[i])}")
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                    }
                }
                if (allGranted) {
                    if (checkLocationService()) {
                        if (isBluetoothEnable()) {
                            onPermissionGranted()
                        } else {
                            startRequestBleEnable()
                        }
                    }
                }
            }
        }
    }

    fun isForbidPermissionPopup(): Boolean {
        return PermissionUtil.isForbidPermissionPopup(this)
    }

    fun shouldShowRequestPermissionRationale(): Boolean {
        return PermissionUtil.shouldShowRequestPermissionRationale(this)
    }

    fun showScanPermissionDetail(needResult: Boolean = true) {
        PermissionUtil.showScanPermissionDetail(this, Intent(activity!!, ScanPermissionDetailActivity::class.java), REQUEST_CODE_PERMISSION_DETAIL, needResult)
    }

    private val mBluetoothStateChangeListener =
            object : DeviceManager.BluetoothAdapterStateChangeListener {
                override fun onStateChange(on: Boolean) {
                    if (!on) {
                        requestPermissionOrStartScan()
                    }
                    onBluetoothStateChange(on)
                }
            }

    fun requestPermissionOrStartScan() {
        if (activity == null) {
            return
        }
        if (hasBluetoothPermissions()) {
            if (isBluetoothEnable()) {
                if (checkLocationService()) {
                    onPermissionGranted()
                }
            } else {
                startRequestBleEnable()
            }
        } else {
            PermissionUtil.requestPermissions(this, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (activity == null) {
            return
        }
        if (requestCode == REQUEST_CODE_FUNCTION_BT) {
            if (isBluetoothEnable()) {
                requestPermissionOrStartScan()
            } else {
                ToastUtils.showShort("蓝牙未开启，无法搜索蓝牙设备")
            }
        } else if (requestCode == REQUEST_CODE_FUNCTION_LOCATION) {
            requestPermissionOrStartScan()
        } else if (requestCode == REQUEST_CODE_PERMISSION_DETAIL) {
            PermissionUtil.requestPermissions(this, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    protected abstract fun onScanStart(success: Boolean)
    protected abstract fun onLeScan(device: BluetoothDevice, blueDevice: BlueDevice, isDeviceVersionValid: Boolean)
    protected abstract fun isLeScanDeviceVersionValid(blueDevice: BlueDevice, scanRecord: ByteArray): Boolean
    protected abstract fun onScanStop()
    protected abstract fun onPermissionGranted()
    protected abstract fun onBluetoothStateChange(on: Boolean)

}