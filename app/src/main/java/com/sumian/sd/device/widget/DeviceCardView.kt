package com.sumian.sd.device.widget

import android.animation.ObjectAnimator
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.sd.R
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.MonitorEventListener
import com.sumian.sd.utils.getString
import com.sumian.sd.widget.dialog.SumianAlertDialog
import com.sumian.sd.widget.dialog.SumianImageTextToast
import kotlinx.android.synthetic.main.layout_device_card_view_device.view.*
import kotlinx.android.synthetic.main.layout_device_card_view_no_device.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:43
 * desc   :
 * version: 1.0
 */
class DeviceCardView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    private var mRotateAnimator: ObjectAnimator? = null
    private val mMonitorEventListener = object : MonitorEventListener {
        override fun onSyncStart() {
            startSyncAnimation()
        }

        override fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int) {
            tv_progress_0.text = resources.getString(R.string.sync_progress_0, packageNumber)
            tv_progress_1.text = resources.getString(R.string.sync_progress_1, progress * 100 / total)
        }

        override fun onSyncSuccess() {
            mRotateAnimator?.cancel()
            showMessageDialog(true, resources.getString(R.string.already_latest_data))
        }

        override fun onSyncFailed() {
            mRotateAnimator?.cancel()
            showMessageDialog(false, resources.getString(R.string.sync_fail))
        }

        override fun onTurnOnPaModeStart() {
        }

        override fun onTurnOnPaModeSuccess() {
            showMessageDialog(true, resources.getString(R.string.sleeper_is_working))
        }

        override fun onTurnOnPaModeFailed(message: String) {
            SumianAlertDialog(context)
                    .hideTopIcon(true)
                    .setTitle(R.string.turn_on_pa_mode_failed_title)
                    .setMessage(resources.getString(R.string.turn_on_pa_mode_failed_desc, message))
                    .setRightBtn(R.string.haode, null)
                    .show()
        }

        override fun onConnectStart() {
            LogUtils.d("onConnectStart")
        }

        override fun onConnectFailed() {
            SumianAlertDialog(context)
                    .hideTopIcon(true)
                    .setTitle(R.string.connect_time_out)
                    .setMessage(R.string.connect_time_out_message)
                    .setRightBtn(R.string.confirm, null)
                    .show()
        }

        override fun onConnectSuccess() {
            LogUtils.d()
        }
    }

    init {
        LogUtils.d("init")
        LayoutInflater.from(context).inflate(R.layout.view_device_card, this, true)
        tv_sync.setOnClickListener { DeviceManager.syncSleepData() }
        bt_turn_on_pa.setOnClickListener { DeviceManager.turnOnSleeperPaMode() }
        setOnClickListener {
            val monitor = DeviceManager.getMonitorLiveData().value
            if (monitor == null) {
                mHost?.scanForDevice()
            } else {
                if (!DeviceManager.isBluetoothEnable()) {
                    mHost?.enableBluetooth()
                } else {
                    if (monitor.status == BlueDevice.STATUS_UNCONNECTED) {
                        DeviceManager.tryToConnectCacheMonitor()
                    }
                }
            }
        }
    }

    fun registerLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        DeviceManager.getMonitorLiveData().observe(lifecycleOwner, Observer {
            LogUtils.d("on monitor status change", it)
            updateUI(DeviceManager.isBluetoothEnable(), it)
        })
        DeviceManager.getIsBluetoothEnableLiveData().observe(lifecycleOwner, Observer {
            updateUI(it ?: false, DeviceManager.getMonitorLiveData().value)
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    private fun updateUI(isBluetoothEnable: Boolean, monitor: BlueDevice?) {
        if (monitor == null) {
            switchNoDeviceUI(true)
            updateNoDeviceUI(R.drawable.ic_home_icon_adddevice, R.string.add_device, R.string.you_do_not_bind_device_click_add)
        } else {
            if (!isBluetoothEnable) {
                switchNoDeviceUI(true)
                updateNoDeviceUI(R.drawable.ic_home_icon_bluetooth, R.string.bluetooth_not_open, R.string.please_open_bluetooth_and_connect_monitor)
            } else {
                showMonitorUI(monitor)
            }
        }
    }

    private fun showMonitorUI(monitor: BlueDevice) {
        when (monitor.status) {
            BlueDevice.STATUS_UNCONNECTED -> {
                switchNoDeviceUI(true)
                updateNoDeviceUI(R.drawable.ic_home_icon_notconnected, R.string.monitor_not_connect, R.string.click_card_try_to_connect_monitor)
            }
            BlueDevice.STATUS_CONNECTING -> {
                switchNoDeviceUI(true)
                updateNoDeviceUI(R.drawable.rotate_device_card_view_connect_device, R.string.monitor_is_connecting, R.string.please_keep_monito_in_5m)
            }
            BlueDevice.STATUS_CONNECTED -> {
                switchNoDeviceUI(false)
                // sync ui
                val isSyncing = monitor.isSyncing
                iv_sync.isEnabled = !isSyncing
                tv_sync.isEnabled = !isSyncing
                tv_sync.text = resources.getString(if (isSyncing) R.string.syncing else R.string.sync)
                tv_progress_0.visibility = if (isSyncing) View.VISIBLE else View.GONE
                tv_progress_1.visibility = if (isSyncing) View.VISIBLE else View.GONE
                if (isSyncing && (mRotateAnimator == null || !mRotateAnimator!!.isRunning)) {
                    startSyncAnimation()
                }

                // monitor ui
                tv_monitor_status.text = getConnectString(monitor.isConnected)
                monitor_battery_view.setProgress(monitor.battery)
                // sleeper ui
                val isPa = monitor.isSleeperPa
                sleeper_battery_view.setProgress(monitor.sleeperBattery)
                tv_speed_sleeper_status.text = getString(when (monitor.sleeperStatus) {
                    BlueDevice.STATUS_CONNECTED -> if (isPa) R.string.working else R.string.already_connected
                    else -> R.string.not_connected
                })
                tv_speed_sleeper_status.setTextColor(ColorCompatUtil.getColor(context, if (isPa) R.color.white else R.color.t2_color))
                tv_speed_sleeper_status.background = if (isPa) resources.getDrawable(R.drawable.sleeper_pa_tv_bg) else null
                tv_bottom_hint.text = resources.getString(if (isPa) R.string.sleeper_is_working_please_sleep else R.string.monitor_is_connect_please_check_sleepers_connectivity)
                tv_bottom_hint.visibility = if (!monitor.isSleeperConnected || isPa) View.VISIBLE else View.GONE
                fl_turn_pa_bt_container.visibility = if (monitor.isSleeperConnected && !isPa) View.VISIBLE else View.GONE
                val isTurningOnPa = monitor.paStatus == BlueDevice.PA_STATUS_TURNING_ON_PA
                bt_turn_on_pa.isEnabled = !isTurningOnPa
                bt_turn_on_pa.setCompoundDrawablesWithIntrinsicBounds(if (isTurningOnPa) R.drawable.rotate_device_card_view_sync else 0, 0, 0, 0)
                bt_turn_on_pa.setText(R.string.starting_work)
            }
        }
    }

    private fun startSyncAnimation() {
        mRotateAnimator?.cancel()
        mRotateAnimator = SyncAnimatorUtil.createSyncRotateAnimator(iv_sync)
        mRotateAnimator?.start()
    }

    private fun updateNoDeviceUI(iconRes: Int, titleRes: Int, messageRes: Int) {
        iv_no_device.setImageResource(iconRes)
        tv_no_device_title.text = getString(titleRes)
        tv_no_device_desc.text = getString(messageRes)
    }

    private fun getConnectString(connected: Boolean) =
            resources.getString(if (connected) R.string.already_connected else R.string.not_connected)

    private fun switchNoDeviceUI(isNoDevice: Boolean) {
        ll_no_device.visibility = if (isNoDevice) VISIBLE else View.GONE
        ll_device.visibility = if (!isNoDevice) VISIBLE else View.GONE
    }

    interface Host {
        fun scanForDevice()
        fun enableBluetooth()
    }

    var mHost: Host? = null

    fun onStart() {
        DeviceManager.addMonitorEventListener(mMonitorEventListener)
    }

    fun onStop() {
        DeviceManager.removeMonitorEventListener(mMonitorEventListener)
    }

    private fun showMessageDialog(success: Boolean, message: String) {
        SumianImageTextToast.showToast(context, if (success) R.drawable.ic_dialog_success else R.drawable.ic_dialog_fail, message, false)
    }
}