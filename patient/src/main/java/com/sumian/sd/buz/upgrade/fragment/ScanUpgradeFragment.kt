package com.sumian.sd.buz.upgrade.fragment

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.liulishuo.filedownloader.BaseDownloadTask
import com.sumian.common.helper.ToastHelper
import com.sumian.device.manager.DeviceManager
import com.sumian.device.util.LogManager
import com.sumian.device.util.MacUtil
import com.sumian.device.util.ScanRecord
import com.sumian.sd.R
import com.sumian.sd.buz.device.scan.BaseScanDeviceFragment
import com.sumian.sd.buz.device.scan.DeviceAdapter
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.buz.upgrade.dialog.UpgradeConfirmDialog
import com.sumian.sd.buz.upgrade.dialog.VersionDialog
import com.sumian.sd.buz.upgrade.manager.DfuUpgradeManager
import com.sumian.sd.buz.version.ui.DeviceUpgradeDialogActivity
import com.sumian.sd.buz.version.ui.DialogManager
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.fragment_scan_device.*
import kotlinx.android.synthetic.main.fragment_scan_upgrade.*
import kotlinx.android.synthetic.main.fragment_scan_upgrade.fl_scan_permission_detail
import kotlinx.android.synthetic.main.fragment_scan_upgrade.iv_bt
import kotlinx.android.synthetic.main.fragment_scan_upgrade.recycler_view
import kotlinx.android.synthetic.main.fragment_scan_upgrade.ripple_view
import kotlinx.android.synthetic.main.fragment_scan_upgrade.tv_rescan
import kotlinx.android.synthetic.main.fragment_scan_upgrade.tv_sub_title
import kotlinx.android.synthetic.main.fragment_scan_upgrade.tv_title
import kotlinx.android.synthetic.main.fragment_scan_upgrade.vg_bt_not_enable
import kotlinx.android.synthetic.main.fragment_scan_upgrade.vg_device_list
import kotlinx.android.synthetic.main.fragment_scan_upgrade.vg_scan
import java.lang.IllegalArgumentException
import java.util.ArrayList

class ScanUpgradeFragment(private var mDeviceType: Int) : BaseScanDeviceFragment() {

    constructor() : this(-1)

    private var mUpgrading: Boolean = false
    private lateinit var mDeviceNamePrefix: String
    private val mScanResults = ArrayList<BlueDevice>()
    private var mFindDeviceSuccess = false
    private var mSaveInstance = false

    private var mProgressDialog: VersionDialog? = null

    private val mDeviceAdapter = DeviceAdapter().apply {
        setOnItemClickListener { v, position, blueDevice ->
            mFindDeviceSuccess = true
            stopScan()
            upgradeDfuModelDevice(blueDevice.mac, getTypeFromDeviceName(blueDevice.name))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("device_type", mDeviceType)
        mSaveInstance = true
    }

    private val mUpgradeCallback = object : DfuUpgradeManager.UpgradeCallback {

        override fun onStart() {
            if (mUpgrading) {
                return
            }
            mUpgrading = true
            LogManager.deviceUpgradeLog("升级固件开始：$mDeviceType")
            if (mProgressDialog?.isAdded == true || mSaveInstance) {
            }else {
                mProgressDialog?.show(activity!!.supportFragmentManager, mProgressDialog?.javaClass?.simpleName)
            }
        }

        override fun onProgressChange(progress: Int) {
            mProgressDialog?.updateProgress(progress)
        }

        override fun onSuccess() {
            mUpgrading = false
            LogManager.deviceUpgradeLog("升级固件成功：$mDeviceType")
            if (mProgressDialog?.isAdded == true) {
                mProgressDialog?.dismissAllowingStateLoss()
            }
            var upgradeConfirmDialog: UpgradeConfirmDialog? = null
            upgradeConfirmDialog = UpgradeConfirmDialog(
                    getString(R.string.upgrade_success_title_text),
                    getString(R.string.upgrade_success_content_text)) {
                upgradeConfirmDialog?.dismissAllowingStateLoss()
                activity?.finish()
            }
            upgradeConfirmDialog.isCancelable = false
            if (activity == null || upgradeConfirmDialog?.isAdded || mSaveInstance) {
            }else {
                upgradeConfirmDialog?.show(activity!!.supportFragmentManager, upgradeConfirmDialog.javaClass.simpleName)
            }
            upgradeSuccess()
        }

        override fun onFail(code: Int, msg: String?) {
            mUpgrading = false
            LogManager.deviceUpgradeLog("升级固件失败：code: $code msg: $msg mDeviceType: $mDeviceType")
            if (mProgressDialog?.isAdded == true) {
                mProgressDialog?.dismissAllowingStateLoss()
            }
            var upgradeConfirmDialog: UpgradeConfirmDialog? = null
            upgradeConfirmDialog = UpgradeConfirmDialog(
                    getString(R.string.upgrade_fail_title_text),
                    getString(R.string.upgrade_fail_content_text)) {
                upgradeConfirmDialog?.dismissAllowingStateLoss()
                rescan()
            }
            if (activity != null) {
                upgradeConfirmDialog?.show(activity!!.supportFragmentManager, upgradeConfirmDialog?.javaClass?.simpleName)
            }
        }
    }

    companion object {
        private const val DFU_SERVICE_UUID = "fe59"
    }

    override fun onScanStart(success: Boolean) {
        mFindDeviceSuccess = false
        mDeviceNamePrefix = when (mDeviceType) {
            DfuUpgradeManager.TYPE_ALL -> "$MONITOR_NAME/$SLEEP_MASTER_NAME"
            DfuUpgradeManager.TYPE_MONITOR -> MONITOR_NAME
            DfuUpgradeManager.TYPE_SLEEP_MASTER -> SLEEP_MASTER_NAME
            else -> "$MONITOR_NAME/$SLEEP_MASTER_NAME"
        }
    }

    override fun onLeScan(device: BluetoothDevice, blueDevice: BlueDevice, isDeviceVersionValid: Boolean) {
        if (isDeviceVersionValid) {
            var type = mDeviceType
            var sumianDevice = DeviceManager.getDevice()
            var mac: String? = null
            var isMacValid = when (type) {
                DfuUpgradeManager.TYPE_ALL -> {
                    if (!mScanResults.contains(blueDevice)) {
                        mScanResults.add(blueDevice)
                        mDeviceAdapter.setData(mScanResults)
                    }
                    switchDeviceListUI(true)
                    false
                }
                DfuUpgradeManager.TYPE_MONITOR -> {
                    mac = sumianDevice?.monitorMac
                    mac?.isNotEmpty() ?: false
                }
                DfuUpgradeManager.TYPE_SLEEP_MASTER -> {
                    mac = sumianDevice?.sleepMasterMac
                    mac?.isNotEmpty() ?: false
                }
                else -> {
                    false
                }
            }
            if (isMacValid) {
                if (MacUtil.getLongMacFromStringMac(blueDevice.mac) - 1 == mac?.let { MacUtil.getLongMacFromStringMac(it) }) {
                    mFindDeviceSuccess = true
                    stopScan()
                    upgradeDfuModelDevice(blueDevice.mac, getTypeFromDeviceName(blueDevice.name))
                }
            }
        }
    }

    override fun isLeScanDeviceVersionValid(blueDevice: BlueDevice, scanRecord: ByteArray): Boolean {
        var isTargetDeviceType = false
        var name: String? = blueDevice.name
        var prefixes = mDeviceNamePrefix.split("/")
        for (prefix in prefixes) {
            isTargetDeviceType = isTargetDeviceType or name!!.startsWith(prefix)
        }
        return if (isTargetDeviceType) {
            var scanRecordBean = ScanRecord.parseFromBytes(scanRecord)
            return if (scanRecordBean?.serviceUuids != null) {
                scanRecordBean.serviceUuids.toString().contains(DFU_SERVICE_UUID)
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun onScanStop() {
        if (!mFindDeviceSuccess && !mFragmentDestroy) {
            if (isBluetoothEnableAndHasPermissions()) {
                startScanWithUi()
            }
        }
    }

    override fun onPermissionGranted() {
        switchDeviceListUI(false)
        startScanWithUi()
    }

    override fun onResume() {
        super.onResume()
        DialogManager.isInScanUpgradeUi = true
    }

    override fun onPause() {
        super.onPause()
        DialogManager.isInScanUpgradeUi = false
    }

    override fun onBluetoothStateChange(on: Boolean) {
        if (!isBluetoothEnableAndHasPermissions()) {
            showEnableBluetoothUI()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_scan_upgrade
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
        tv_rescan.setOnClickListener { rescan() }
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mDeviceAdapter
        if (!isBluetoothEnableAndHasPermissions()) {
            showEnableBluetoothUI()
        }
        mProgressDialog = VersionDialog.newInstance(getString(R.string.firmware_upgrade_title_hint)) as VersionDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSaveInstance = false
        mDeviceType = savedInstanceState?.getInt("device_type", mDeviceType) ?: mDeviceType
    }

    private fun rescan() {
        mFindDeviceSuccess = false
        mScanResults.clear()
        if (DeviceManager.isScanning()) {
            stopScan()
        } else {
            startScanWithUi()
        }
        switchDeviceListUI(false)
    }

    private fun startScanWithUi() {
        ripple_view.startAnimation()
        startScan()
    }

    private fun showEnableBluetoothUI() {
        hideVgs()
        fl_scan_permission_detail.visibility = View.VISIBLE
        vg_bt_not_enable.visibility = View.VISIBLE
        setTitles(R.string.open_bluetooth, R.string.please_turn_on_bluetooth_adapter)
    }

    private fun setTitles(@StringRes title: Int, @StringRes subTitle: Int) {
        setTitles(getString(title), if (subTitle == 0) "" else getString(subTitle))
    }

    private fun setTitles(title: String, subTitle: String) {
        tv_title.text = title
        tv_sub_title.text = subTitle
    }

    private fun switchDeviceListUI(showDeviceList: Boolean) {
        hideVgs()
        setTitles(R.string.scan_upgrade_title, R.string.scan_upgrade_content)
        vg_device_list.visibility = if (showDeviceList) View.VISIBLE else View.GONE
        vg_scan.visibility = if (!showDeviceList) View.VISIBLE else View.GONE
    }

    private fun hideVgs() {
        vg_bt_not_enable.visibility = View.GONE
        vg_scan.visibility = View.INVISIBLE
        vg_device_list.visibility = View.GONE
        fl_scan_permission_detail.visibility = View.GONE
    }

    private fun upgradeDfuModelDevice(dfuMac: String, type: Int) {
        DfuUpgradeManager.downloadUpgradeFile(object : DfuUpgradeManager.DownloadCallback {
            override fun started(task: BaseDownloadTask?) {
            }

            override fun onCompleted(path: String) {
                if (mUpgrading) {
                    return
                }
                DfuUpgradeManager.upgradeDfuDevice(dfuMac, path,
                        onStart = { mUpgradeCallback.onStart() },
                        onProgressChange = { progress -> mUpgradeCallback.onProgressChange(progress) },
                        onSuccess = { mUpgradeCallback.onSuccess() },
                        onFail = { code, msg -> mUpgradeCallback.onFail(code, msg) })
            }

            override fun onError(e: Throwable?) {
                mUpgradeCallback.onFail(DfuUpgradeManager.UpgradeCallback.ERROR_CODE_DOWNLOAD_FILE_FAIL, e?.message)
            }

            override fun onProgress(soFarBytes: Int, totalBytes: Int) {
            }

            override fun onPaused(soFarBytes: Int, totalBytes: Int) {
            }
        }, type)
    }

    private fun getTypeFromDeviceName(name: String): Int {
        return when {
            name.startsWith(MONITOR_NAME) -> DfuUpgradeManager.TYPE_MONITOR
            name.startsWith(SLEEP_MASTER_NAME) -> DfuUpgradeManager.TYPE_SLEEP_MASTER
            else -> throw IllegalArgumentException()
        }
    }

    private fun upgradeSuccess() {
        ToastHelper.show(when (mDeviceType) {
            DfuUpgradeManager.TYPE_SLEEP_MASTER -> R.string.sleeper_firmware_upgrade_success_hint
            else -> R.string.firmware_upgrade_success_hint
        })
        LogManager.deviceUpgradeLog("设备dfu固件升级完成")
        DfuUpgradeManager.reconnectDevice()
        EventBusUtil.postEvent(DeviceUpgradeDialogActivity.DfuUpgradeSuccessEvent())
    }
}