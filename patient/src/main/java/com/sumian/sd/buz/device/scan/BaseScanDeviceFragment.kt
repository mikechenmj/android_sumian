package com.sumian.sd.buz.device.scan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.device.callback.ScanCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.utils.LocationManagerUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

abstract class BaseScanDeviceFragment : BaseFragment() {

    protected var mScanning: Boolean = false
    protected var mFragmentDestroy = false

    companion object {
        const val REQUEST_CODE_FUNCTION_BT = 1
        const val REQUEST_CODE_FUNCTION_LOCATION = 2
        const val REQUEST_PERMISSION_LOCATION_AND_STORAGE = 3
        const val MONITOR_NAME = "M-SUMIAN"
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
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_FUNCTION_BT)
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
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return EasyPermissions.hasPermissions(activity!!, *perms)
    }

    protected fun checkLocationService(): Boolean {
        val locationProviderEnable = LocationManagerUtil.isLocationProviderEnable(context!!)
        return if (locationProviderEnable) {
            true
        } else {
            SumianDialog(context!!)
                    .setTitleText(R.string.open_location_service_dialog_title)
                    .setMessageText(R.string.open_location_service_for_blue_scan_hint)
                    .setRightBtn(R.string.confirm, View.OnClickListener {
                        LocationManagerUtil.startLocationSettingActivityForResult(this, REQUEST_CODE_FUNCTION_LOCATION)
                    })
                    .setCanceledOnTouchOutsideWrap(false)
                    .show()
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DeviceManager.registerBluetoothAdapterStateChangeListener(mBluetoothStateChangeListener)
        checkScanPermission()
    }

    override fun onDestroyView() {
        DeviceManager.unregisterBluetoothAdapterStateChangeListener(mBluetoothStateChangeListener)
        mFragmentDestroy = true
        stopScan()
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private val mBluetoothStateChangeListener =
            object : DeviceManager.BluetoothAdapterStateChangeListener {
                override fun onStateChange(on: Boolean) {
                    checkScanPermission()
                    onBluetoothStateChange(on)
                }
            }

    @AfterPermissionGranted(REQUEST_PERMISSION_LOCATION_AND_STORAGE)
    private fun checkScanPermission() {
        if (!isBluetoothEnable()) {
            return
        }
        val perms = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(activity!!, *perms)) {
            if (checkLocationService()) {
                onPermissionGranted()
            }
        } else {
            EasyPermissions.requestPermissions(this, resources.getString(R.string.request_permission_hint), REQUEST_PERMISSION_LOCATION_AND_STORAGE, *perms)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FUNCTION_BT) {
            if (isBluetoothEnable()) {
                checkScanPermission()
            } else {
                ToastUtils.showShort("蓝牙未开启，无法搜索蓝牙设备")
            }
        } else if (requestCode == REQUEST_CODE_FUNCTION_LOCATION) {
            checkScanPermission()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    protected abstract fun onScanStart(success: Boolean)
    protected abstract fun onLeScan(device: BluetoothDevice, blueDevice: BlueDevice, isDeviceVersionValid: Boolean)
    protected abstract fun isLeScanDeviceVersionValid(blueDevice: BlueDevice, scanRecord: ByteArray): Boolean
    protected abstract fun onScanStop()
    protected abstract fun onPermissionGranted()
    protected abstract fun onBluetoothStateChange(on: Boolean)

}