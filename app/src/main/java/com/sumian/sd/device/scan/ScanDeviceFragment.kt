@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.device.scan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.blue.callback.BlueAdapterCallback
import com.sumian.blue.callback.BlueScanCallback
import com.sumian.common.base.BaseFragment
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sd.device.adapter.DeviceAdapter
import com.sumian.sd.device.bean.BlueDevice
import com.sumian.hw.log.LogManager
import com.sumian.hw.utils.LocationManagerUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
import kotlinx.android.synthetic.main.fragment_scan_device.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 22:23
 * desc   :
 * 搜索逻辑：搜索三秒，如果无结果，继续搜索，直到搜索到一台设备为止（显示一台设备，停止搜索）；如果有一台设备，显示一台设备，停止搜索；如果有多台设备，显示设备列表；
 * 搜索更多逻辑：不断搜索，列表动态显示搜索结果
 * version: 1.0
 */
class ScanDeviceFragment : BaseFragment() {

    private val mBlueManager = AppManager.getBlueManager()
    private val mScanResults = ArrayList<BlueDevice>()
    private val mHandler = Handler()
    private var mIsScanMore = false
    private var mStartScanTime = 0L
    private var mIsScanning = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_scan_device
    }

    companion object {
        private const val REQUEST_CODE_ENABLE_BT = 1
        private const val REQUEST_CODE_ENABLE_LOCATION = 2
        private const val SCAN_CHECK_DURATION = 3000L
        private const val SCAN_DURATION = 17 * 1000L
    }

    private val mDeviceAdapter = DeviceAdapter().apply { setOnItemClickListener { v, position, blueDevice -> onDeviceSelected(blueDevice) } }

    override fun initWidget() {
        super.initWidget()
        iv_bt.setOnClickListener { startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BT) }
        vg_bt_not_enable.visibility = if (mBlueManager.isEnable) View.GONE else View.VISIBLE
        tv_scan_more.setOnClickListener { startScan(true) }
        tv_rescan.setOnClickListener { startScan() }
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mDeviceAdapter
        bt_re_scan.setOnClickListener { startScan() }
        bt_confirm.setOnClickListener { onDeviceSelected(mScanResults[0]) }
        showEnableBtnOrStartScan()
    }

    private val mBlueAdapterEnableListener = object : BlueAdapterCallback {
        override fun onAdapterEnable() {
            showEnableBtnOrStartScan()
        }

        override fun onAdapterDisable() {
            showEnableBtnOrStartScan()
        }
    }

    override fun initData() {
        super.initData()
        mBlueManager.addBlueAdapterCallback(mBlueAdapterEnableListener)
        mBlueManager.addBlueScanCallback(mScanCallback)
    }

    override fun onDestroy() {
        unregisterBlueCallbacks()
        mHandler.removeCallbacks(null)
        stopScanIfIsScanning()
        super.onDestroy()
    }

    private fun unregisterBlueCallbacks() {
        mBlueManager.removeBlueAdapterCallback(mBlueAdapterEnableListener)
        mBlueManager.removeBlueScanCallback(mScanCallback)
    }

    private val mScanCallback = object : BlueScanCallback {

        override fun onBeginScanCallback() {
            mIsScanning = true
        }

        override fun onLeScanCallback(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray?) {
            if (!device.name.startsWith("M-SUMIAN") || rssi <= -80) {
                return
            }
            val blueDevice = BlueDevice()
            blueDevice.name = device.name
            blueDevice.mac = device.address
            blueDevice.rssi = rssi
            val isDeviceVersionValid = DeviceValidateUtil.isDeviceValid(scanRecord, blueDevice.name, blueDevice.mac)
            LogManager.appendBluetoothLog(
                    String.format(Locale.getDefault(),
                            "搜索到 %s %s, isVersionValid: %b",
                            device.name,
                            device.address,
                            isDeviceVersionValid))
            if (isDeviceVersionValid && !mScanResults.contains(blueDevice)) {
                mScanResults.add(blueDevice)
                mDeviceAdapter.setData(mScanResults)
            }
            if (System.currentTimeMillis() - mStartScanTime > SCAN_CHECK_DURATION && !mIsScanMore) {
                stopScanIfIsScanning()
            }
        }

        override fun onFinishScanCallback() {
            mIsScanning = false
            hideVgs()
            when (mScanResults.size) {
                0 -> showNoDeviceUI()
                1 -> showOneDeviceUI()
                else -> showMultiDeviceUI()
            }
        }
    }

    private fun stopScanIfIsScanning() {
        if (mIsScanning) {
            mBlueManager.doStopScan()
        }
        mIsScanning = false
    }

    private fun showNoDeviceUI() {
        setTitles(R.string.do_not_see_your_device, R.string.please_check_items_below)
        vg_no_device.visibility = View.VISIBLE
        LogManager.appendBluetoothLog("该次没有搜索到任何设备")
    }

    private fun showMultiDeviceUI() {
        hideVgs()
        switchDeviceListUI(true)
        mDeviceAdapter.setData(mScanResults)
        LogManager.appendBluetoothLog("该次搜索到" + mScanResults.size + "台设备 " + mScanResults.toString())
    }

    private fun showOneDeviceUI() {
        hideVgs()
        switchDeviceListUI(mIsScanMore)
        if (!mIsScanMore) {
            setTitles(mScanResults[0].name, getString(R.string.is_sure_device_2_bind))
            iv_device.visibility = View.VISIBLE
            bt_confirm.visibility = View.VISIBLE
            vg_scan_more_tvs.visibility = View.VISIBLE
        }
        LogManager.appendBluetoothLog("该次搜索到一台设备 " + mScanResults[0].name + " " + mScanResults[0].mac)
    }

    private fun hideVgs() {
        vg_bt_not_enable.visibility = View.GONE
        vg_no_device.visibility = View.GONE
        vg_scan.visibility = View.INVISIBLE
        vg_device_list.visibility = View.GONE
        iv_device.visibility = View.GONE
        bt_confirm.visibility = View.GONE
    }

    private fun setTitles(@StringRes title: Int, @StringRes subTitle: Int) {
        setTitles(getString(title), if (subTitle == 0) "" else getString(subTitle))
    }

    private fun setTitles(title: String, subTitle: String) {
        tv_title.text = title
        tv_sub_title.text = subTitle
    }

    @AfterPermissionGranted(REQUEST_CODE_ENABLE_BT)
    private fun checkPermissionStartScan() {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(activity!!, *perms)) {
            if (checkLocationService()) {
                startScan()
            }
        } else {
            EasyPermissions.requestPermissions(this, resources.getString(R.string.request_permission_hint), REQUEST_CODE_ENABLE_BT, *perms)
        }
    }

    private fun checkLocationService(): Boolean {
        val locationProviderEnable = LocationManagerUtil.isLocationProviderEnable(context!!)
        return if (locationProviderEnable) {
            true
        } else {
            SumianDialog(context!!)
                    .setTitleText(R.string.open_location_service_dialog_title)
                    .setMessageText(R.string.open_location_service_for_blue_scan_hint)
                    .setRightBtn(R.string.confirm, View.OnClickListener { LocationManagerUtil.startLocationSettingActivityForResult(this@ScanDeviceFragment, REQUEST_CODE_ENABLE_LOCATION) })
                    .setCanceledOnTouchOutsideV2(false)
                    .show()
            false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_ENABLE_BT) {
            if (mBlueManager.isEnable) {
                checkPermissionStartScan()
            } else {
                ToastUtils.showShort("蓝牙未开启，无法搜索蓝牙设备")
            }
        } else if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
            checkPermissionStartScan()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startScan(isScanMore: Boolean = false) {
        if (!isScanMore) {
            resetScanResults()
            mHandler.postDelayed({ checkScanResult() }, SCAN_CHECK_DURATION)
            iv_device.visibility = View.GONE
            vg_scan_more_tvs.visibility = View.GONE
            ripple_view.startAnimation()
        }

        mIsScanMore = isScanMore
        mBlueManager.startScanAndAutoStopAfter(SCAN_DURATION)
        mStartScanTime = System.currentTimeMillis()
        switchDeviceListUI(isScanMore)
    }

    private fun checkScanResult() {
        if (mScanResults.size == 1) {
            stopScanIfIsScanning()
        } else if (mScanResults.size > 1) {
            showMultiDeviceUI()
        }
    }

    private fun resetScanResults() {
        mScanResults.clear()
        mDeviceAdapter.clear()
    }

    private fun onDeviceSelected(device: BlueDevice) {
        unregisterBlueCallbacks()
        stopScanIfIsScanning()
        DeviceManager.cacheBlueDevice(device)
        mOnDeviceSelectedListener?.onDeviceSelected(device)
    }

    fun rollback() {
        showEnableBtnOrStartScan()
    }

    private fun showEnableBtnOrStartScan() {
        if (mBlueManager.isEnable) {
            checkPermissionStartScan()
        } else {
            hideVgs()
            vg_bt_not_enable.visibility = View.VISIBLE
            setTitles(R.string.open_bluetooth, R.string.please_turn_on_bluetooth_adapter)
        }
    }

    interface OnDeviceSelectedListener {
        fun onDeviceSelected(device: BlueDevice)
    }

    var mOnDeviceSelectedListener: OnDeviceSelectedListener? = null

    /**
     * 两种状态：正常搜索， 搜索更多
     */
    private fun switchDeviceListUI(showDeviceList: Boolean) {
        hideVgs()
        if (showDeviceList) {
            setTitles(R.string.bind_device, R.string.select_bind_device_label)
        } else {
            setTitles(R.string.scanning_device, R.string.scan_label_h2)
        }
        vg_device_list.visibility = if (showDeviceList) View.VISIBLE else View.GONE
        vg_scan.visibility = if (!showDeviceList) View.VISIBLE else View.GONE
    }
}