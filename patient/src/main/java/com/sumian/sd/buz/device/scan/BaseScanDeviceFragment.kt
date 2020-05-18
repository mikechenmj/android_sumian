package com.sumian.sd.buz.device.scan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.device.callback.ScanCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.utils.LocationManagerUtil
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

abstract class BaseScanDeviceFragment : BaseFragment() {

    protected var mScanning: Boolean = false
    protected var mFragmentDestroy = false
    private val mPerms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    else {
        arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    companion object {
        const val REQUEST_CODE_FUNCTION_BT = 1
        const val REQUEST_CODE_FUNCTION_LOCATION = 2
        const val REQUEST_CODE_PERMISSION_DETAIL = 3
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
        SdLogManager.logPermission("startRequestBleEnable")
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
        return EasyPermissions.hasPermissions(activity!!, *mPerms)
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
        requestPermissionIfNeed()
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
                    SdLogManager.logPermission("requestPermissionIfNeed permission: ${ActivityCompat.checkSelfPermission(activity!!, permissions[i])}")
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
        for (permission in mPerms) {
            if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED
                    && !shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }

    fun shouldShowRequestPermissionRationale(): Boolean {
        for (permission in mPerms) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }

    fun showScanPermissionDetail(needResult: Boolean = true) {
        var intent = Intent(activity!!, ScanPermissionDetailActivity::class.java)
        if (needResult) {
            startActivityForResult(intent, REQUEST_CODE_PERMISSION_DETAIL)
        } else {
            startActivity(intent)
        }
    }

    private val mBluetoothStateChangeListener =
            object : DeviceManager.BluetoothAdapterStateChangeListener {
                override fun onStateChange(on: Boolean) {
                    requestPermissionIfNeed()
                    onBluetoothStateChange(on)
                }
            }

    private fun requestPermissionIfNeed() {
        if (activity == null) {
            return
        }
        if (EasyPermissions.hasPermissions(activity!!, *mPerms)) {
            if (checkLocationService()) {
                if (isBluetoothEnable()) {
                    onPermissionGranted()
                } else {
                    startRequestBleEnable()
                }
            }
        } else {
            requestPermissions(mPerms, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (activity == null) {
            return
        }
        if (requestCode == REQUEST_CODE_FUNCTION_BT) {
            if (isBluetoothEnable()) {
                requestPermissionIfNeed()
            } else {
                ToastUtils.showShort("蓝牙未开启，无法搜索蓝牙设备")
            }
        } else if (requestCode == REQUEST_CODE_FUNCTION_LOCATION) {
            requestPermissionIfNeed()
        } else if (requestCode == REQUEST_CODE_PERMISSION_DETAIL) {
            requestPermissions(mPerms, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
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