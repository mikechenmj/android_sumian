package com.sumian.devicedemo.device

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
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.SleepMasterWorkModeStatus
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseFragment
import com.sumian.devicedemo.sleepdata.SleepDataActivity
import com.sumian.devicedemo.util.SyncAnimatorUtil
import com.sumian.devicedemo.widget.SumianImageTextToast
import kotlinx.android.synthetic.main.fragment_device_card.*
import kotlinx.android.synthetic.main.layout_device_card_view_device.*
import kotlinx.android.synthetic.main.layout_device_card_view_no_device.*


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
        return R.layout.fragment_device_card
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
        mRootView.setOnClickListener {
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
            tv_device_data.setOnClickListener {
                startActivity(Intent(this.context, SleepDataActivity::class.java))
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
                    tv_progress.text = "${device?.syncProgress ?: 0 * 100 /
                    (device?.syncTotalCount ?: 1)}%"
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
                // sync ui
                val isSyncing = device.isSyncing
                iv_sync.isEnabled = !isSyncing
                tv_sync.isEnabled = !isSyncing
                tv_sync.text =
                        resources.getString(if (isSyncing) R.string.syncing else R.string.sync)
                tv_progress.visibility = if (isSyncing) View.VISIBLE else View.GONE
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

                tv_bottom_hint.text =
                        getString(if (isWorkModeOn) R.string.sleeper_is_working_please_sleep else R.string.monitor_is_connect_please_check_sleepers_connectivity)
                tv_bottom_hint.visibility =
                        if (!device.isSleepMasterConnected() || isWorkModeOn) View.VISIBLE else View.GONE



                fl_turn_pa_bt_container.visibility =
                        if (device.isSleepMasterConnected() && !isWorkModeOn) View.VISIBLE else View.GONE
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

    private fun isAppNeedUpgrade() {

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
                DeviceManager.bind(
                        ScanDeviceActivity.getDeviceMacFromIntent(data!!)!!,
                        mConnectDeviceCallback
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}