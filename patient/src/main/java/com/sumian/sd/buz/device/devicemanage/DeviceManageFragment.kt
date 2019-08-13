package com.sumian.sd.buz.device.devicemanage

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.data.SumianDevice
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.buz.device.widget.SyncAnimatorUtil
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.common.utils.BluetoothUtil
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.fragment_device_manage.*
import kotlinx.android.synthetic.main.layout_device_manage_fragment_no_device.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/14 5:41
 * desc   :
 * version: 1.0
 */
class DeviceManageFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_device_manage
    }

    companion object {
        const val CARD_STATUS_NO_DEVICE = 0
        const val CARD_STATUS_MONITOR_NOT_CONNECTED = 1
        const val CARD_STATUS_MONITOR_CONNECTED = 2
        const val CARD_STATUS_BLUETOOTH_NOT_ENABLE = 3

        const val REQUEST_CODE_OPEN_BLUETOOTH = 1
    }

    private var mCardStatus = 0
    private var mRotateAnimator: ObjectAnimator? = null
    private var mMonitor: BlueDevice? = null
    private var mPopupWindow: PopupWindow? = null

    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String) {
            when (type) {
                DeviceManager.EVENT_SYNC_SLEEP_DATA_START -> startSyncAnimation()
                DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL -> {
                    mRotateAnimator?.cancel()
                    dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_FAIL, resources.getString(R.string.sync_fail), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS -> {
                    mRotateAnimator?.cancel()
                    dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_SUCCESS, resources.getString(R.string.already_latest_data), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE -> {

                }
                DeviceManager.EVENT_MONITOR_CONNECT_STATUS_CHANGE -> {

                }
            }
            updateUI()
        }
    }

    private fun startSyncAnimation() {
        mRotateAnimator?.cancel()
        mRotateAnimator = SyncAnimatorUtil.createSyncRotateAnimator(iv_device_bg)
        mRotateAnimator?.start()
    }

    private fun showRipple(show: Boolean) {
        if (show) {
            ripple_view?.startAnimation()
        }
        ripple_view.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun initWidget() {
        super.initWidget()
        iv_add_device.setOnClickListener { mHost?.scanForDevice() }
        bt_turn_on_pa.setOnClickListener {
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_LOADING)
            DeviceManager.toggleSleeperWorkMode(true, object : AsyncCallback<Any?> {
                override fun onSuccess(data: Any?) {
                    dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_SUCCESS, resources.getString(R.string.sleeper_is_working), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
                }

                override fun onFail(code: Int, msg: String) {
                    dismissSumianImageTextDialog()
                    SumianAlertDialog(context)
                            .hideTopIcon(true)
                            .setTitle(R.string.turn_on_pa_mode_failed_title)
                            .setMessage(resources.getString(R.string.turn_on_pa_mode_failed_desc, msg))
                            .setRightBtn(R.string.haode, null)
                            .show()
                }
            })
        }
        iv_device.setOnClickListener {
            if (!DeviceManager.isMonitorConnected()) {
                DeviceManager.connectBoundDevice(object : ConnectDeviceCallback {
                    override fun onStart() {
                        showRipple(true)
                    }

                    override fun onSuccess() {
                        showRipple(false)
                    }

                    override fun onFail(code: Int, msg: String) {
                        showRipple(false)
                        SumianAlertDialog(context)
                                .hideTopIcon(true)
                                .setTitle(R.string.connect_time_out)
                                .setMessage(msg)
                                .setRightBtn(R.string.confirm, null)
                                .show()
                    }
                })
            }
        }
        iv_float_menu.setOnClickListener { showUnbindPopup() }
        iv_open_bluetooth.setOnClickListener {
            // 由于监听了蓝牙装填的live data， 蓝牙状态改变会自动update ui
            BluetoothUtil.startActivityForOpenBluetooth(this, REQUEST_CODE_OPEN_BLUETOOTH)
        }
        monitor_battery_view.setTextSize(14f)
        sleeper_battery_view.setTextSize(14f)
    }

    @SuppressLint("InflateParams")
    private fun showUnbindPopup() {
        val inflate = LayoutInflater.from(activity!!).inflate(R.layout.layout_undbind_popup, null, false)
        inflate.setOnClickListener {
            SumianAlertDialog(activity)
                    .hideTopIcon(true)
                    .setTitle(R.string.sure_to_unbind)
                    .setMessage(R.string.after_unbind_monitor_will_disconnect_from_phone)
                    .setLeftBtn(R.string.cancel, null)
                    .setRightBtn(R.string.confirm) {
                        DeviceManager.unbind()
                        mPopupWindow?.dismiss()
                    }
                    .show()
        }
        mPopupWindow = PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mPopupWindow?.isOutsideTouchable = true
        mPopupWindow?.isFocusable = true
        mPopupWindow?.showAsDropDown(iv_float_menu)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
    }

    override fun onDetach() {
        DeviceManager.unregisterDeviceStatusListener(mDeviceStatusListener)
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val isBluetoothEnable = DeviceManager.isBluetoothEnable()
        val device = DeviceManager.getDevice()
        LogUtils.d(device)
        if (device == null) {
            mCardStatus = CARD_STATUS_NO_DEVICE
            showAddDeviceOrOpenBluetoothUI(true)
        } else if (!isBluetoothEnable) {
            mCardStatus = CARD_STATUS_BLUETOOTH_NOT_ENABLE
            showAddDeviceOrOpenBluetoothUI(false)
        } else {
            mCardStatus = if (device.isMonitorConnected()) CARD_STATUS_MONITOR_CONNECTED else CARD_STATUS_MONITOR_NOT_CONNECTED
            switchNoDeviceVg(false)
            updateMonitorUI(device)
            updateSleeperUI(device)
            updateDeviceIv(device)
            updateBottomTv(device)
        }
    }

    private fun updateDeviceIv(monitor: SumianDevice) {
        val deviceIvRes =
                if (monitor.isSyncing) {
                    R.drawable.ic_equip_icon_monitor_synchronization
                } else {
                    if (monitor.isSleepMasterConnected())
                        R.drawable.ic_equip_icon_sleeper
                    else
                        R.drawable.ic_equip_icon_monitor
                }
        iv_device.setImageResource(deviceIvRes)
        iv_device_bg.setImageResource(if (monitor.isSyncing) R.drawable.ic_equip_bg_synchronization else R.drawable.ic_equip_bg)
        iv_device_bg.visibility = if (monitor.monitorConnectStatus == DeviceConnectStatus.CONNECTING) View.GONE else View.VISIBLE
        iv_device.alpha = if (monitor.monitorConnectStatus == DeviceConnectStatus.DISCONNECTED) .5f else 1f
    }

    private fun showAddDeviceOrOpenBluetoothUI(showAddDevice: Boolean) {
        switchNoDeviceVg(true)
        tv_title.setText(if (showAddDevice) R.string.add_device else R.string.open_bluetooth)
        tv_sub_title.setText(if (showAddDevice) R.string.please_keep_nearly else R.string.please_turn_on_bluetooth_adapter)
        tv_add_device_hint.setText(if (showAddDevice) R.string.click_btn_add_device else R.string.bluetooth_not_enable)
        iv_add_device.visibility = if (showAddDevice) View.VISIBLE else View.GONE
        iv_open_bluetooth.visibility = if (!showAddDevice) View.VISIBLE else View.GONE
    }

    private fun updateMonitorUI(monitor: SumianDevice) {
        tv_monitor_status.text = resources.getString(when (monitor.monitorConnectStatus) {
            DeviceConnectStatus.DISCONNECTED -> R.string.not_connected
            DeviceConnectStatus.CONNECTING -> R.string.connecting
            DeviceConnectStatus.CONNECTED -> R.string.connected
            else -> R.string.not_connected
        })
        monitor_battery_view.setProgress(monitor.monitorBattery)
        vg_monitor.alpha = if (monitor.monitorConnectStatus == DeviceConnectStatus.DISCONNECTED) .5f else 1f
        vg_sleeper.visibility = if (monitor.monitorConnectStatus == DeviceConnectStatus.CONNECTED) View.VISIBLE else View.INVISIBLE
        if (monitor.isSyncing) {
            if ((mRotateAnimator == null || !mRotateAnimator!!.isRunning)) {
                startSyncAnimation()
            }
        } else {
            if (mRotateAnimator != null && mRotateAnimator?.isRunning!!) {
                mRotateAnimator?.cancel()
            }
        }
        showRipple(monitor.monitorConnectStatus == DeviceConnectStatus.CONNECTING)
        tv_monitor.setTextColor(ColorCompatUtil.getColor(activity!!, if (monitor.isMonitorConnected()) R.color.t3_color else R.color.t2_color))
        tv_sleeper.setTextColor(ColorCompatUtil.getColor(activity!!, if (monitor.isSleepMasterConnected()) R.color.t3_color else R.color.t2_color))
    }

    private fun updateBottomTv(monitor: SumianDevice) {


        val monitorCompatibility = DeviceManager.checkMonitorVersionCompatibility()
        val sleepMasterCompatibility = DeviceManager.checkSleepMasterVersionCompatibility()
        val appNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
                || sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_HIGH
        val monitorNeedUpgrade = monitorCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
        val sleepMasterNeedUpgrade = sleepMasterCompatibility == DeviceManager.PROTOCOL_VERSION_TO_LOW
        val deviceNeedUpgrade = monitorNeedUpgrade || sleepMasterNeedUpgrade

        tv_bottom_hint.isVisible = !monitor.isSyncing || appNeedUpgrade || deviceNeedUpgrade
        tv_bottom_hint.text =
                getString(when {
                    appNeedUpgrade -> if (monitorNeedUpgrade) R.string.device_is_not_ok_app_need_upgrade else R.string.sleep_master_is_not_ok_app_need_upgrade
                    deviceNeedUpgrade -> if (monitorNeedUpgrade) R.string.device_is_not_ok_device_need_upgrade else R.string.sleep_master_is_not_ok_device_need_upgrade
                    else -> if (monitor.monitorConnectStatus == DeviceConnectStatus.DISCONNECTED) {
                        R.string.monitor_not_connect_click_upper_to_connect
                    } else if (monitor.monitorConnectStatus == DeviceConnectStatus.CONNECTING) {
                        R.string.monitor_is_connecting
                    } else {
                        if (monitor.sleepMasterConnectStatus == DeviceConnectStatus.DISCONNECTED) {
                            R.string.monitor_is_connected_please_check_sleeper
                        } else {
                            if (monitor.isSleepMasterWorkModeOn()) R.string.sleeper_is_working else R.string.sleeper_is_idle
                        }
                    }
                })

        tv_bottom_progress.text = getString(R.string.sync_progress_package_progress, 0, monitor.syncProgress * 100 / monitor.syncTotalCount)
        tv_bottom_progress.visibility = if (monitor.isSyncing && !appNeedUpgrade && !deviceNeedUpgrade) View.VISIBLE else View.GONE

        bt_turn_on_pa.isVisible = monitor.isMonitorConnected() && !monitor.isSyncing
                && monitor.isSleepMasterConnected()
                && !monitor.isSleepMasterWorkModeOn() && !appNeedUpgrade && !deviceNeedUpgrade
    }

    private fun updateSleeperUI(monitor: SumianDevice?) {
        // sleeper ui
        sleeper_battery_view.setProgress(monitor?.sleepMasterBattery ?: 0)
        tv_speed_sleeper_status.text = getString(when (monitor?.sleepMasterConnectStatus) {
            DeviceConnectStatus.CONNECTED -> if (monitor.isSleepMasterWorkModeOn()) R.string.working else R.string.already_connected
            DeviceConnectStatus.DISCONNECTED -> R.string.not_connected
            else -> R.string.not_connected
        })
    }

    private fun switchNoDeviceVg(isNoDevice: Boolean) {
        vg_no_device.visibility = if (isNoDevice) View.VISIBLE else View.GONE
        ll_device.visibility = if (!isNoDevice) View.VISIBLE else View.GONE
    }

    private var mSumianImageTextDialog: SumianImageTextDialog? = null


    private fun dismissOldCreateNewSumianImageTextDialog(): SumianImageTextDialog {
        dismissSumianImageTextDialog()
        mSumianImageTextDialog = SumianImageTextDialog(activity!!)
        return mSumianImageTextDialog!!
    }

    private fun dismissSumianImageTextDialog() {
        if (mSumianImageTextDialog != null && mSumianImageTextDialog!!.isShowing) {
            mSumianImageTextDialog?.dismiss()
            mSumianImageTextDialog = null
        }
    }

    interface Host {
        fun scanForDevice()
        fun showBluetoothNotEnableUI()
    }

    var mHost: Host? = null

    override fun onStop() {
        super.onStop()
        dismissSumianImageTextDialog()
    }
}