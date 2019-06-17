package com.sumian.sd.buz.device.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.statistic.StatUtil
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.SleepMasterWorkModeStatus
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.device.scan.ScanDeviceActivity
import com.sumian.sd.buz.diary.DataFragment
import com.sumian.sd.buz.diary.event.ChangeDataFragmentTabEvent
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.main.MainActivity
import com.sumian.sd.main.event.ChangeMainTabEvent
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
    }

    private var mRotateAnimator: ObjectAnimator? = null

    override fun getLayoutId(): Int {
        return R.layout.view_device_card
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_sync.setOnClickListener { DeviceManager.startSyncSleepData() }
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
                if (!DeviceManager.isBluetoothEnable()) {
                    DeviceManager.enableBluetooth()
                } else {
                    if (monitor.monitorConnectStatus == DeviceConnectStatus.DISCONNECTED) {
                        DeviceManager.connectBoundDevice(mConnectDeviceCallback)
                    }
                }
            }
        }
        tv_device_data.setOnClickListener {
            StatUtil.event(StatConstants.click_home_page_device_data_icon)
            EventBusUtil.postStickyEvent(ChangeMainTabEvent(MainActivity.TAB_1))
            EventBusUtil.postStickyEvent(ChangeDataFragmentTabEvent(DataFragment.TAB_1))
        }
        tv_know_device.setOnClickListener {
            StatUtil.event(StatConstants.click_home_page_learn_more_about_device)
            MiniProgramHelper.launchYouZanOrWeb(activity!!)
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
            ToastUtils.showShort(msg)
            updateDevice()
        }
    }

    private val mDeviceStatusListener = object : DeviceStatusListener {
        @SuppressLint("SetTextI18n")
        override fun onStatusChange(type: String) {
            val device = DeviceManager.getDevice()
            when (type) {
                DeviceManager.EVENT_SYNC_SLEEP_DATA_START -> {
                    startSyncAnimation()
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS -> {
                    stopSyncAnimation()
                    showMessageDialog(true, resources.getString(R.string.already_latest_data))
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL
                -> {
                    stopSyncAnimation()
                    showMessageDialog(false, resources.getString(R.string.sync_fail))
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
        vg_no_device.visibility = if (!deviceNotConnected) View.VISIBLE else View.GONE
        tv_know_device.visibility = if (device == null) View.VISIBLE else View.GONE
        tv_device_data.visibility = if (device != null) View.VISIBLE else View.GONE
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
                val appNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
                        || sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
                val monitorNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
                val sleepMasterNeedUpgrade = sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
                val deviceNeedUpgrade = monitorNeedUpgrade || sleepMasterNeedUpgrade

                // sync ui
                vg_sync.isVisible = !appNeedUpgrade && !deviceNeedUpgrade
                val isSyncing = device.isSyncing
                iv_sync.isEnabled = !isSyncing
                tv_sync.isEnabled = !isSyncing
                tv_sync.text =
                        resources.getString(if (isSyncing) R.string.syncing else R.string.sync)
                tv_progress.visibility = if (isSyncing) View.VISIBLE else View.GONE
                tv_progress.text = "${device.syncProgress * 100 / device.syncTotalCount}%"
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
                tv_bottom_hint.visibility =
                        if (!device.isSleepMasterConnected() || isWorkModeOn || appNeedUpgrade || deviceNeedUpgrade) View.VISIBLE else View.GONE
                tv_bottom_hint.text =
                        getString(when {
                            appNeedUpgrade -> if (monitorNeedUpgrade) R.string.device_is_not_ok_app_need_upgrade else R.string.sleep_master_is_not_ok_app_need_upgrade
                            deviceNeedUpgrade -> if (monitorNeedUpgrade) R.string.device_is_not_ok_device_need_upgrade else R.string.sleep_master_is_not_ok_device_need_upgrade
                            else -> if (isWorkModeOn) R.string.sleeper_is_working_please_sleep else R.string.monitor_is_connect_please_check_sleepers_connectivity
                        })

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
        if (requestCode == REQUEST_CODE_SCAN_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
//                DeviceManager.bind(
//                        ScanDeviceActivity.getDeviceMacFromIntent(data!!)!!,
//                        mConnectDeviceCallback
//                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}