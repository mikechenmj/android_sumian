package com.sumian.sd.device

import android.animation.ObjectAnimator
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.sd.R
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.fragment_device_manage.*

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

    private val mMonitorEventListener = object : MonitorEventListener {
        override fun onSyncStart() {
            mRotateAnimator?.cancel()
            mRotateAnimator = ObjectAnimator.ofFloat(iv_device_bg, "rotation", 0f, 360 * 100f)
            mRotateAnimator?.duration = 1000 * 100
            mRotateAnimator?.start()
        }

        override fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int) {
            tv_bottom.text = getString(R.string.sync_progress_package_progress, packageNumber, progress * 100 / total)
        }

        override fun onSyncSuccess() {
            mRotateAnimator?.cancel()
        }

        override fun onSyncFailed() {
            mRotateAnimator?.cancel()
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_FAIL, resources.getString(R.string.sync_fail), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)

        }

        override fun onTurnOnPaModeStart() {
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_LOADING)
        }

        override fun onTurnOnPaModeSuccess() {
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_SUCCESS, resources.getString(R.string.sleeper_is_working), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
        }

        override fun onTurnOnPaModeFailed(message: String) {
            dismissSumianImageTextDialog()
            SumianAlertDialog(context)
                    .hideTopIcon(true)
                    .setTitle(R.string.turn_on_pa_mode_failed_title)
                    .setMessage(resources.getString(R.string.turn_on_pa_mode_failed_desc, message))
                    .setRightBtn(R.string.haode, null)
                    .show()
        }

        override fun onConnectStart() {
            showRipple(true)
        }

        override fun onConnectFailed() {
            showRipple(false)
        }

        override fun onConnectSuccess() {
            showRipple(false)
        }
    }

    private fun showRipple(show: Boolean) {
        if (show) {
            ripple_view.startAnimation()
        }
        ripple_view.visibility = if (show) View.VISIBLE else View.GONE
        iv_device_bg.visibility = if (!show) View.VISIBLE else View.GONE
    }

    init {
        DeviceManager.addMonitorEventListener(mMonitorEventListener)
        registerLifecycleOwner(this)
    }

    override fun initWidget() {
        super.initWidget()
        iv_add_device.setOnClickListener { mHost?.scanForDevice() }
        bt_turn_on_pa.setOnClickListener { DeviceManager.turnOnSleeperPaMode() }
        iv_device.setOnClickListener {
            if (mMonitor != null) {
                return@setOnClickListener
            }
            if (mMonitor!!.status == BlueDevice.STATUS_UNCONNECTED) {
                DeviceManager.scanAndConnect(mMonitor!!)
            }
        }
        iv_float_menu.setOnClickListener { showUnbindPopup() }
        iv_open_bluetooth.setOnClickListener {
            // 由于监听了蓝牙装填的live data， 蓝牙状态改变会自动update ui
            BluetoothUtil.startActivityForOpenBluetooth(this, REQUEST_CODE_OPEN_BLUETOOTH)
        }
    }

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

    private fun registerLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        DeviceManager.getMonitorLiveData().observe(lifecycleOwner, Observer {
            mMonitor = it
            updateUI(DeviceManager.isBluetoothEnable(), it)
        })
        DeviceManager.getIsBluetoothEnableLiveData().observe(lifecycleOwner, Observer {
            updateUI(it ?: false, DeviceManager.getMonitorLiveData().value)
        })
    }

    override fun onDetach() {
        DeviceManager.removeMonitorEventListener(mMonitorEventListener)
        super.onDetach()
    }

    private fun updateUI(isBluetoothEnable: Boolean, monitor: BlueDevice?) {
        LogUtils.d(monitor)
        if (monitor == null) {
            mCardStatus = CARD_STATUS_NO_DEVICE
            showAddDeviceOrOpenBluetoothUI(true)
        } else if (!isBluetoothEnable) {
            mCardStatus = CARD_STATUS_BLUETOOTH_NOT_ENABLE
            showAddDeviceOrOpenBluetoothUI(false)
        } else {
            mCardStatus = if (monitor.isConnected) CARD_STATUS_MONITOR_CONNECTED else CARD_STATUS_MONITOR_NOT_CONNECTED
            switchNoDeviceVg(false)
            showMonitorUI(monitor)
            showSleeperUI(monitor.speedSleeper)
            updateDeviceIv(monitor)
            updateBottomTv(monitor)
        }
    }

    private fun updateDeviceIv(monitor: BlueDevice) {
        val deviceIvRes =
                if (monitor.isSyncing) {
                    R.drawable.ic_equip_icon_monitor_synchronization
                } else {
                    if (monitor.isSleeperConnected)
                        R.drawable.ic_equip_icon_sleeper
                    else
                        R.drawable.ic_equip_icon_monitor
                }
        iv_device.setImageResource(deviceIvRes)
        iv_device_bg.setImageResource(if (monitor.isSyncing) R.drawable.ic_equip_bg_synchronization else R.drawable.ic_equip_bg)
        iv_device.alpha = if (monitor.status == BlueDevice.STATUS_UNCONNECTED) .5f else 1f
        bt_turn_on_pa.visibility = if (monitor.isConnected && monitor.isSleeperConnected && !monitor.isSleeperPa) View.VISIBLE else View.GONE
    }

    private fun showAddDeviceOrOpenBluetoothUI(showAddDevice: Boolean) {
        switchNoDeviceVg(true)
        tv_title.setText(if (showAddDevice) R.string.add_device else R.string.open_bluetooth)
        tv_sub_title.setText(if (showAddDevice) R.string.please_keep_nearly else R.string.please_turn_on_bluetooth_adapter)
        tv_add_device_hint.setText(if (showAddDevice) R.string.click_btn_add_device else R.string.bluetooth_not_enable)
        iv_add_device.visibility = if (showAddDevice) View.VISIBLE else View.GONE
        iv_open_bluetooth.visibility = if (!showAddDevice) View.VISIBLE else View.GONE
    }

    private fun showMonitorUI(monitor: BlueDevice) {
        tv_monitor_status.text = resources.getString(when (monitor.status) {
            BlueDevice.STATUS_UNCONNECTED -> R.string.not_connected
            BlueDevice.STATUS_CONNECTING -> R.string.connecting
            BlueDevice.STATUS_CONNECTED -> R.string.connected
            else -> R.string.not_connected
        })
        monitor_battery_view.setProgress(monitor.battery)
        vg_monitor.alpha = if (monitor.status == BlueDevice.STATUS_UNCONNECTED) .5f else 1f
        vg_sleeper.visibility = if (monitor.status == BlueDevice.STATUS_CONNECTED) View.VISIBLE else View.INVISIBLE
    }

    private fun updateBottomTv(monitor: BlueDevice) {
        tv_bottom.text = getString(if (monitor.status == BlueDevice.STATUS_UNCONNECTED) {
            R.string.monitor_not_connect_click_upper_to_connect
        } else if (monitor.status == BlueDevice.STATUS_CONNECTING) {
            R.string.monitor_is_connecting
        } else {
            if (monitor.speedSleeper?.status == BlueDevice.STATUS_UNCONNECTED) {
                R.string.monitor_is_connected_please_check_sleeper
            } else {
                if (monitor.isSleeperPa) R.string.sleeper_is_working else R.string.sleeper_is_idle
            }
        })
    }

    private fun showSleeperUI(sleeper: BlueDevice?) {
        // sleeper ui
        sleeper_battery_view.setProgress(sleeper?.battery ?: 0)
        tv_speed_sleeper_status.text = getString(when (sleeper?.status) {
            BlueDevice.STATUS_CONNECTED -> if (sleeper.isPa) R.string.working else R.string.already_connected
            BlueDevice.STATUS_UNCONNECTED -> R.string.not_connected
            else -> R.string.not_connected
        })
    }

    private fun switchNoDeviceVg(isNoDevice: Boolean) {
        ll_no_device.visibility = if (isNoDevice) View.VISIBLE else View.GONE
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