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
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.MonitorEventListener
import com.sumian.sd.utils.getString
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.view_device_card.view.*


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:43
 * desc   :
 * version: 1.0
 */
class DeviceCardView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    companion object {
        const val CARD_STATUS_NO_DEVICE = 0
        const val CARD_STATUS_MONITOR_NOT_CONNECTED = 1
        const val CARD_STATUS_MONITOR_CONNECTED = 2
        const val CARD_STATUS_BLUETOOTH_NOT_ENABLE = 3
    }

    private var mCardStatus = 0
    private var mRotateAnimator: ObjectAnimator? = null

    private val mMonitorEventListener = object : MonitorEventListener {
        override fun onSyncStart() {
            mRotateAnimator?.cancel()
            mRotateAnimator = ObjectAnimator.ofFloat(iv_sync, "rotation", 0f, 360 * 100f)
            mRotateAnimator?.duration = 1000 * 100
            mRotateAnimator?.start()
        }

        override fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int) {
            tv_progress_0.text = resources.getString(R.string.sync_progress_0, packageNumber)
            tv_progress_1.text = resources.getString(R.string.sync_progress_1, progress)
        }

        override fun onSyncSuccess() {
            mRotateAnimator?.cancel()
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_SUCCESS, resources.getString(R.string.already_latest_data), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
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
            LogUtils.d("onConnectStart")
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_LOADING)
        }

        override fun onConnectFailed() {
            dismissSumianImageTextDialog()
        }

        override fun onConnectSuccess() {
            LogUtils.d()
            dismissSumianImageTextDialog()
        }
    }

    init {
        LogUtils.d("init")
        LayoutInflater.from(context).inflate(R.layout.view_device_card, this, true)
        tv_sync.setOnClickListener { DeviceManager.syncSleepData() }
        bt_turn_on_pa.setOnClickListener { DeviceManager.turnOnSleeperPaMode() }
        setOnClickListener {
            when (mCardStatus) {
                CARD_STATUS_NO_DEVICE -> mHost?.scanForDevice()
                CARD_STATUS_MONITOR_NOT_CONNECTED -> DeviceManager.tryToConnectCacheMonitor()
                CARD_STATUS_BLUETOOTH_NOT_ENABLE -> mHost?.enableBluetooth()
                else -> Unit
            }
        }
        DeviceManager.addMonitorEventListener(mMonitorEventListener)
    }

    fun registerLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        DeviceManager.getMonitorLiveData().observe(lifecycleOwner, Observer {
            updateUI(DeviceManager.isBluetoothEnable(), it)
        })
        DeviceManager.getIsBluetoothEnableLiveData().observe(lifecycleOwner, Observer {
            updateUI(it ?: false, DeviceManager.getMonitorLiveData().value)
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        DeviceManager.removeMonitorEventListener(mMonitorEventListener)
    }

    private fun updateUI(isBluetoothEnable: Boolean, monitor: BlueDevice?) {
        if (monitor == null) {
            mCardStatus = CARD_STATUS_NO_DEVICE
            showNoDeviceUI()
        } else if (!isBluetoothEnable) {
            mCardStatus = CARD_STATUS_BLUETOOTH_NOT_ENABLE
            showBluetoothNotEnableUI()
        } else {
            mCardStatus = if (monitor.isConnected) CARD_STATUS_MONITOR_CONNECTED else CARD_STATUS_MONITOR_NOT_CONNECTED
            showMonitorUI(monitor)
        }
    }

    private fun showNoDeviceUI() {
        switchNoDeviceUI(true)
        setNoDeviceUI(R.drawable.ic_home_icon_adddevice, R.string.add_device, R.string.you_do_not_bind_device_click_add)
    }

    private fun showBluetoothNotEnableUI() {
        switchNoDeviceUI(true)
        setNoDeviceUI(R.drawable.ic_home_icon_bluetooth, R.string.bluetooth_not_open, R.string.please_open_bluetooth_and_connect_monitor)
    }

    private fun showMonitorUI(monitor: BlueDevice) {
        switchNoDeviceUI(!monitor.isConnected)
        if (monitor.isConnected) {
            var sleeper = monitor.speedSleeper
            // sync ui
            val isSyncing = monitor.isSyncing
            tv_sync.isEnabled = !isSyncing
            tv_sync.text = resources.getString(if (isSyncing) R.string.syncing else R.string.sync)
            tv_progress_0.visibility = if (isSyncing) View.VISIBLE else View.GONE
            tv_progress_1.visibility = if (isSyncing) View.VISIBLE else View.GONE
            // monitor ui
            tv_monitor_status.text = getConnectString(monitor.isConnected)
            monitor_battery_view.setProgress(monitor.battery)
            // sleeper ui
            if (sleeper == null) {
                sleeper = BlueDevice()
                sleeper.name = App.getAppContext().resources.getString(R.string.speed_sleeper)
                sleeper.status = BlueDevice.STATUS_UNCONNECTED
            }
            showSleeperUI(sleeper)
        } else {
            setNoDeviceUI(R.drawable.ic_home_icon_notconnected, R.string.monitor_not_connect, R.string.click_card_try_to_connect_monitor)
        }
        LogUtils.d(monitor)
    }

    private fun setNoDeviceUI(iconRes: Int, titleRes: Int, messageRes: Int) {
        iv_no_device.setImageResource(iconRes)
        tv_no_device_title.text = getString(titleRes)
        tv_no_device_desc.text = getString(messageRes)
    }

    private fun getConnectString(connected: Boolean) =
            resources.getString(if (connected) R.string.already_connected else R.string.not_connected)

    private fun showSleeperUI(sleeper: BlueDevice) {
        // sleeper ui
        sleeper_battery_view.setProgress(sleeper.battery)
        tv_speed_sleeper_status.text = getString(when (sleeper.status) {
            BlueDevice.STATUS_CONNECTED -> if (sleeper.isPa) R.string.working else R.string.already_connected
            BlueDevice.STATUS_UNCONNECTED -> R.string.not_connected
            else -> R.string.not_connected
        })
        tv_speed_sleeper_status.setTextColor(resources.getColor(if (sleeper.isPa) R.color.white else R.color.t2_color))
        tv_speed_sleeper_status.background = if (sleeper.isPa) resources.getDrawable(R.drawable.sleeper_pa_tv_bg) else null
        tv_bottom_hint.text = resources.getString(if (sleeper.isPa) R.string.sleeper_is_working_please_sleep else R.string.monitor_is_connect_please_sleepers_connectivity)
        tv_bottom_hint.visibility = if (!sleeper.isConnected || sleeper.isPa) View.VISIBLE else View.GONE
        fl_turn_pa_bt_container.visibility = if (sleeper.isConnected && !sleeper.isPa) View.VISIBLE else View.GONE
    }

    private fun switchNoDeviceUI(isNoDevice: Boolean) {
        ll_no_device.visibility = if (isNoDevice) VISIBLE else View.GONE
        ll_device.visibility = if (!isNoDevice) VISIBLE else View.GONE
    }

    private var mSumianImageTextDialog: SumianImageTextDialog? = null


    private fun dismissOldCreateNewSumianImageTextDialog(): SumianImageTextDialog {
        dismissSumianImageTextDialog()
        mSumianImageTextDialog = SumianImageTextDialog(context)
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
        fun enableBluetooth()
    }

    var mHost: Host? = null

}