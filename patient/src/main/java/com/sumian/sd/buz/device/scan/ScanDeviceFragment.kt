@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.device.scan

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.statistic.StatUtil
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.LogManager
import kotlinx.android.synthetic.main.fragment_scan_device.*
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
class ScanDeviceFragment : BaseScanDeviceFragment() {

    private val mScanResults = ArrayList<BlueDevice>()
    private val mHandler = Handler()
    private var mIsScanMore = false
    private var mHasCheckResult = false

    private val mDeviceAdapter = DeviceAdapter().apply {
        setOnItemClickListener { v, position, blueDevice -> bindDevice(blueDevice) }
    }

    companion object {
        private const val SCAN_CHECK_DURATION = 3000L + DeviceManager.SCAN_DELAY
    }

    override fun onScanStart(success: Boolean) {
    }

    override fun onLeScan(device: BluetoothDevice, blueDevice: BlueDevice, isDeviceVersionValid: Boolean) {
        if (isDeviceVersionValid && !mScanResults.contains(blueDevice)) {
            LogUtils.d(mScanResults, blueDevice.name)
            mScanResults.add(blueDevice)
            mDeviceAdapter.setData(mScanResults)
        }
        if (mHasCheckResult && !mIsScanMore) {
            checkScanResult()
        }
    }

    override fun isLeScanDeviceVersionValid(blueDevice: BlueDevice, scanRecord: ByteArray): Boolean {
        return if (blueDevice.name.startsWith(MONITOR_NAME)) {
            DeviceValidateUtil.isDeviceValid(scanRecord, blueDevice.name, blueDevice.mac)
        } else {
            false
        }

    }

    override fun onScanStop() {
        removeCheckScanResultRunnable()
        if (vg_bt_not_enable != null) {
            hideVgs()
            when (mScanResults.size) {
                0 -> showNoDeviceUI()
                1 -> showOneDeviceUI()
                else -> showMultiDeviceUI()
            }
        }
    }

    override fun onPermissionGranted() {
        startScanWithUi()
    }

    override fun onBluetoothStateChange(on: Boolean) {
        if (!isBluetoothEnableAndHasPermissions()) {
            showEnableBluetoothUI()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_scan_device
    }

    override fun initWidget() {
        super.initWidget()
        iv_bt.setOnClickListener {
            if (isForbidPermissionPopup() || shouldShowRequestPermissionRationale()) {
                showScanPermissionDetail()
            } else {
                startRequestBleEnable()
            }
        }
        fl_scan_permission_detail.setOnClickListener { showScanPermissionDetail(false) }
        tv_scan_more.setOnClickListener {
            mIsScanMore = true
            requestPermissionOrStartScan()
        }
        tv_rescan.setOnClickListener {
            mIsScanMore = false
            requestPermissionOrStartScan()
        }
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mDeviceAdapter
        bt_re_scan.setOnClickListener {
            mIsScanMore = false
            requestPermissionOrStartScan()
        }
        bt_confirm.setOnClickListener {
            StatUtil.event(StatConstants.click_scan_device_page_confirm_bind_btn)
            bindDevice(mScanResults[0])
        }
        if (!isBluetoothEnableAndHasPermissions()) {
            showEnableBluetoothUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeCheckScanResultRunnable()
        stopScan()
    }

    private fun sendCheckScanResultRunnable(delay: Long = SCAN_CHECK_DURATION) {
        removeCheckScanResultRunnable()
        mHandler.postDelayed(mCheckScanResultRunnable, delay)
    }

    private fun removeCheckScanResultRunnable() {
        mHandler.removeCallbacks(mCheckScanResultRunnable)
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
        StatUtil.trackBeginPage(activity!!, StatConstants.page_scan_device)
        LogManager.appendBluetoothLog("该次搜索到一台设备 " + mScanResults[0].name + " " + mScanResults[0].mac)
    }

    private fun hideVgs() {
        vg_bt_not_enable.visibility = View.GONE
        vg_no_device.visibility = View.GONE
        vg_scan.visibility = View.INVISIBLE
        vg_device_list.visibility = View.GONE
        iv_device.visibility = View.GONE
        bt_confirm.visibility = View.GONE
        fl_scan_permission_detail.visibility = View.GONE
    }

    private fun setTitles(@StringRes title: Int, @StringRes subTitle: Int) {
        setTitles(getString(title), if (subTitle == 0) "" else getString(subTitle))
    }

    private fun setTitles(title: String, subTitle: String) {
        tv_title.text = title
        tv_sub_title.text = subTitle
    }

    private fun startScanWithUi() {
        if (!mIsScanMore) {
            resetScanResults()
            sendCheckScanResultRunnable()
            iv_device.visibility = View.GONE
            vg_scan_more_tvs.visibility = View.GONE
            ripple_view.startAnimation()
        }
        startScan()
        switchDeviceListUI(mIsScanMore)
    }

    private val mCheckScanResultRunnable = Runnable {
        checkScanResult()
    }

    private fun checkScanResult() {
        mHasCheckResult = true
        if (mScanResults.size == 1) {
            stopScan()
        } else if (mScanResults.size > 1) {
            showMultiDeviceUI()
        }
    }

    private fun resetScanResults() {
        mHasCheckResult = false
        mScanResults.clear()
        mDeviceAdapter.clear()
    }

    private fun bindDevice(device: BlueDevice) {
        StatUtil.event(StatConstants.on_bind_device_success)
        stopScan()
        DeviceManager.bind(device.mac, object : ConnectDeviceCallback {
            override fun onStart() {
            }

            override fun onSuccess() {
            }

            override fun onFail(code: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
        mOnDeviceSelectedListener?.onDeviceSelected(device)
    }

    private fun showEnableBluetoothUI() {
        hideVgs()
        vg_bt_not_enable.visibility = View.VISIBLE
        fl_scan_permission_detail.visibility = View.VISIBLE
        setTitles(R.string.open_bluetooth, R.string.please_turn_on_bluetooth_adapter)
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