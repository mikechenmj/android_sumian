package com.sumian.sd.buz.device.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.LocationManagerUtil
import com.sumian.common.utils.PermissionUtil
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.SleepMasterWorkModeStatus
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.SyncSleepDataHelper
import com.sumian.sd.R
import com.sumian.sd.buz.device.scan.ScanDeviceActivity
import com.sumian.sd.buz.device.scan.ScanPermissionDetailActivity
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.utils.UiUtils
import com.sumian.sd.widget.dialog.SumianImageTextToast
import com.sumian.sd.wxapi.MiniProgramHelper
import kotlinx.android.synthetic.main.layout_device_card_view_device.*
import kotlinx.android.synthetic.main.layout_device_card_view_no_device.*
import kotlinx.android.synthetic.main.view_device_card.*


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:43
 * desc   :
 * version: 1.0
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class DeviceCardFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SCAN_DEVICE = 100
        private const val REQUEST_CODE_FUNCTION_LOCATION = 200
        private const val REQUEST_CODE_PERMISSION_DETAIL = 300
        private const val REQUEST_PERMISSION_LOCATION_AND_STORAGE = 1
    }

    private var mRotateAnimator: ObjectAnimator? = null

    override fun getLayoutId(): Int {
        return R.layout.view_device_card
    }

    private val mSyncResultListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String) {
            if (type == DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS) {
                showMessageDialog(true, resources.getString(R.string.already_latest_data))
                DeviceManager.unregisterDeviceStatusListener(this)
            } else if (type == DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL) {
                showMessageDialog(false, resources.getString(R.string.sync_fail))
                DeviceManager.unregisterDeviceStatusListener(this)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_sync.setOnClickListener {
            var state = DeviceManager.startSyncSleepData()
            if (state == SyncSleepDataHelper.SyncState.START || state == SyncSleepDataHelper.SyncState.RETRY) {
                DeviceManager.registerDeviceStatusListener(mSyncResultListener)
            } else if (state == SyncSleepDataHelper.SyncState.FAIL_IS_SYNCING) {
                showMessageDialog(true, resources.getString(R.string.already_latest_data))
            } else if (state == SyncSleepDataHelper.SyncState.FAIL_VERSION_WRONG) {
                showMessageDialog(false, resources.getString(R.string.sync_fail))
            }
        }
        bt_turn_on_pa.setOnClickListener {
            DeviceManager.toggleSleeperWorkMode(true, callback = object : AsyncCallback<Any?> {
                override fun onSuccess(data: Any?) {
                    updateDevice()
                }

                override fun onFail(code: Int, msg: String) {
                    ToastUtils.showShort(msg)
                }
            })
        }
        vg_device_card_view.setOnClickListener {
            val monitor = DeviceManager.getDevice()
            if (monitor == null) {
                ScanDeviceActivity.startForResult(
                        this,
                        REQUEST_CODE_SCAN_DEVICE
                )
            } else {
                connectBoundDeviceIfCan(monitor)
            }
        }
        tv_know_device.setOnClickListener {
            StatUtil.event(StatConstants.click_home_page_learn_more_about_device)
            MiniProgramHelper.launchYouZanOrWeb(activity!!)
        }
    }

    private fun connectBoundDeviceIfCan(device: SumianDevice) {
        if (PermissionUtil.hasBluetoothPermissions(activity!!)) {
            if (LocationManagerUtil.checkLocationService(this, REQUEST_CODE_FUNCTION_LOCATION)) {
                if (!DeviceManager.isBluetoothEnable()) {
                    DeviceManager.enableBluetooth()
                } else {
                    if (device.monitorConnectStatus == DeviceConnectStatus.DISCONNECTED) {
                        DeviceManager.connectBoundDevice(mConnectDeviceCallback)
                    }
                }
            }
        } else {
            if (PermissionUtil.isForbidPermissionPopup(this) || PermissionUtil.shouldShowRequestPermissionRationale(this)) {
                PermissionUtil.showScanPermissionDetail(this, Intent(activity!!, ScanPermissionDetailActivity::class.java), REQUEST_CODE_PERMISSION_DETAIL)
            } else {
                PermissionUtil.requestPermissions(this, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
            }
        }
    }

    private val mConnectDeviceCallback = object : ConnectDeviceCallback {
        override fun onStart() {
            updateDevice()
        }

        override fun onSuccess() {
            updateDevice()
        }

        override fun onFail(code: Int, msg: String) {
            updateDevice()
        }
    }

    private val mDeviceStatusListener = object : DeviceStatusListener {
        @SuppressLint("SetTextI18n")
        override fun onStatusChange(type: String) {
            when (type) {
                DeviceManager.EVENT_SYNC_SLEEP_DATA_START -> {
                    startSyncAnimation()
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS -> {
                    stopSyncAnimation()
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL
                -> {
                    stopSyncAnimation()
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE -> {
                }
            }
            updateDevice()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val onCreateView = super.onCreateView(inflater, container, savedInstanceState)
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
        return onCreateView
    }

    override fun onResume() {
        super.onResume()
        updateDevice()
    }

    override fun onDestroyView() {
        DeviceManager.unregisterDeviceStatusListener(mDeviceStatusListener)
        super.onDestroyView()
    }

    private fun startSyncAnimation() {
        mRotateAnimator?.cancel()
        mRotateAnimator =
                SyncAnimatorUtil.createSyncRotateAnimator(iv_sync)
        mRotateAnimator?.start()
    }

    private fun stopSyncAnimation() {
        mRotateAnimator?.cancel()
    }

    private fun updateNoDeviceUI(iconRes: Int, titleRes: Int, messageRes: Int) {
        iv_no_device.setImageResource(iconRes)
        tv_no_device_title.text = resources.getString(titleRes)
        tv_no_device_desc.text = resources.getString(messageRes)
    }

    private fun getConnectString(connected: Boolean) =
            resources.getString(if (connected) R.string.already_connected else R.string.not_connected)


    fun updateDevice() {
        updateUI(DeviceManager.getDevice())
    }

    private fun updateUI(device: SumianDevice?) {
        val isBluetoothEnable = DeviceManager.isBluetoothEnable()
        val deviceNotConnected =
                device != null && isBluetoothEnable && device.isMonitorConnected()
        ll_device.visibility = if (deviceNotConnected) View.VISIBLE else View.GONE
        ll_sync.visibility = if (deviceNotConnected) View.VISIBLE else View.GONE
        vg_no_device.visibility = if (!deviceNotConnected) View.VISIBLE else View.GONE
        tv_know_device.visibility = if (device == null) View.VISIBLE else View.GONE
        if (device == null) {
            updateNoDeviceUI(
                    R.drawable.ic_home_icon_adddevice,
                    R.string.add_device,
                    R.string.you_do_not_bind_device_click_add
            )
        } else {
            if (!isBluetoothEnable) {
                updateNoDeviceUI(
                        R.drawable.ic_home_icon_bluetooth,
                        R.string.bluetooth_not_open,
                        R.string.please_open_bluetooth_and_connect_monitor
                )
            } else {
                updateDeviceUI(device)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDeviceUI(device: SumianDevice) {
        when (device.monitorConnectStatus) {
            DeviceConnectStatus.DISCONNECTED -> {
                updateNoDeviceUI(
                        R.drawable.ic_home_icon_notconnected,
                        R.string.monitor_not_connect,
                        R.string.click_card_try_to_connect_monitor
                )
            }
            DeviceConnectStatus.CONNECTING -> {
                updateNoDeviceUI(
                        R.drawable.rotate_device_card_view_connect_device,
                        R.string.monitor_is_connecting,
                        R.string.please_keep_monito_in_5m
                )
            }
            DeviceConnectStatus.CONNECTED -> {
                val monitorCompatibility = DeviceManager.checkMonitorVersionCompatibility()
                val sleepMasterCompatibility = DeviceManager.checkSleepMasterVersionCompatibility()
                var appNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
                        || sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
                val monitorNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
                var sleepMasterNeedUpgrade = sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
                var deviceNeedUpgrade = monitorNeedUpgrade || sleepMasterNeedUpgrade

                // sync ui
                vg_sync.isVisible = !appNeedUpgrade && !deviceNeedUpgrade
                val isSyncing = device.isSyncing
                iv_sync.isEnabled = !isSyncing
                tv_sync.isEnabled = !isSyncing
                tv_sync.text =
                        resources.getString(if (isSyncing) R.string.syncing else R.string.sync)
                tv_sync_progress.visibility = if (isSyncing) View.VISIBLE else View.GONE
                tv_sync_progress.text = "${device.syncProgress * 100 / device.syncTotalCount}%"
                if (isSyncing && (mRotateAnimator == null || !mRotateAnimator!!.isRunning)) {
                    startSyncAnimation()
                }
                // monitor ui
                tv_monitor_status.text = getConnectString(device.isMonitorConnected())
                monitor_battery_view.setProgress(device.monitorBattery)

                // sleeper ui
                val isWorkModeOn = device.isSleepMasterWorkModeOn()
                sleeper_battery_view.setProgress(device.sleepMasterBattery)
                tv_sleep_master_status.text = getString(
                        when (device.sleepMasterConnectStatus) {
                            DeviceConnectStatus.CONNECTED -> if (isWorkModeOn) R.string.working else R.string.already_connected
                            else -> R.string.not_connected
                        }
                )
                tv_sleep_master_status.setTextColor(
                        activity!!.resources.getColor(if (isWorkModeOn) R.color.white else R.color.t2_color)
                )
                @Suppress("DEPRECATION")
                tv_sleep_master_status.background =
                        if (isWorkModeOn) resources.getDrawable(R.drawable.bg_sleeper_pa_tv) else null

                // bottom tv
                val appOrDeviceNeedUpgrade = appNeedUpgrade || monitorNeedUpgrade || sleepMasterNeedUpgrade
                tv_bottom_hint.isVisible = (!device.isSleepMasterConnected() || isWorkModeOn) && !appOrDeviceNeedUpgrade
                tv_bottom_hint.text = getString(if (isWorkModeOn) R.string.sleeper_is_working_please_sleep else R.string.monitor_is_connect_please_check_sleepers_connectivity)
                vg_bottom_upgrade_hint.isVisible = appOrDeviceNeedUpgrade
                tv_bottom_upgrade_hint.setText(
                        when {
                            appNeedUpgrade -> R.string.app_version_too_low
                            monitorNeedUpgrade -> R.string.device_version_too_low
                            sleepMasterNeedUpgrade -> R.string.sleep_master_version_too_low
                            else -> R.string.device_version_too_low
                        })
                tv_bottom_upgrade_hint2.setText(if (appNeedUpgrade) R.string.go_to_upgrade_app else R.string.go_to_upgrade_device)
                tv_bottom_upgrade_hint2.setOnClickListener {
                    if (appNeedUpgrade) {
                        UiUtils.openAppInMarket(activity!!)
                    } else {
                        ActivityUtils.startActivity(DeviceVersionNoticeActivity::class.java)
                    }
                }

                fl_turn_pa_bt_container.isVisible = device.isSleepMasterConnected() && !isWorkModeOn && !appNeedUpgrade && !deviceNeedUpgrade
                val isTurningOnPa =
                        device.sleepMasterWorkModeStatus == SleepMasterWorkModeStatus.TURNING_ON
                bt_turn_on_pa.isEnabled = !isTurningOnPa
                bt_turn_on_pa.setCompoundDrawablesWithIntrinsicBounds(
                        if (isTurningOnPa) R.drawable.rotate_device_card_view_sync else 0,
                        0,
                        0,
                        0
                )
                bt_turn_on_pa.setText(if (isTurningOnPa) R.string.starting_work else R.string.start_work)
            }
            else -> {
            }
        }
    }

    private fun showMessageDialog(success: Boolean, message: String?) {
        SumianImageTextToast.showToast(
                activity!!,
                if (success) R.drawable.ic_dialog_success else R.drawable.ic_dialog_fail,
                message,
                false
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_FUNCTION_LOCATION -> {
                var device = DeviceManager.getDevice() ?: return
                connectBoundDeviceIfCan(device)
            }
            REQUEST_CODE_PERMISSION_DETAIL -> {
                PermissionUtil.requestPermissions(this, REQUEST_PERMISSION_LOCATION_AND_STORAGE)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === REQUEST_PERMISSION_LOCATION_AND_STORAGE) {
            var allGranted = true
            for (i in permissions.indices) {
                SdLogManager.logPermission("requestPermissionIfNeed permission: ${ActivityCompat.checkSelfPermission(activity!!, permissions[i])}")
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                }
            }
            if (allGranted) {
                DeviceManager.getDevice() ?: return
                connectBoundDeviceIfCan(DeviceManager.getDevice()!!)
            }
        }
    }
}