package com.sumian.sd.device.widget

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.sd.R
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.MonitorEventListener
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

    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_device_card, this, true)
        tv_sync.setOnClickListener { DeviceManager.syncSleepData() }
        bt_turn_on_pa.setOnClickListener { DeviceManager.turnOnSleeperPaMode() }
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
        DeviceManager.addMonitorEventListener(mMonitorEventListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        DeviceManager.removeMonitorEventListener(mMonitorEventListener)
    }

    private fun updateUI(isBluetoothEnable: Boolean, monitor: BlueDevice?) {
        if (monitor == null) {
            showNoDeviceUI()
        } else if (!isBluetoothEnable) {
            showBluetoothNotEnableUI()
        } else {
            showMonitorUI(monitor)
        }
    }

    private fun showNoDeviceUI() {
        switchNoDeviceUI(true)
    }

    private fun showBluetoothNotEnableUI() {
        switchNoDeviceUI(true)
    }

    private fun showMonitorUI(monitor: BlueDevice) {
        switchNoDeviceUI(false)
        val sleeper = monitor.speedSleeper
        // sync ui
        tv_sync.isEnabled = !monitor.isSyncing
        tv_sync.text = resources.getString(if (monitor.isSyncing) R.string.syncing else R.string.sync)
        tv_progress_0.visibility = if (monitor.isSyncing) View.VISIBLE else View.GONE
        tv_progress_1.visibility = if (monitor.isSyncing) View.VISIBLE else View.GONE
        // monitor ui
        tv_monitor_status.text = getConnectString(monitor.isConnected)
        monitor_battery_view.setProgress(monitor.battery)
        // sleeper ui
        showSleeperUI(sleeper)
    }

    private fun getConnectString(connected: Boolean) =
            resources.getString(if (connected) R.string.already_connected else R.string.not_connected)

    private fun showSleeperUI(sleeper: BlueDevice) {
        // sleeper ui
        sleeper_battery_view.setProgress(sleeper.battery)
        tv_speed_sleeper_status.text = getConnectString(sleeper.isConnected)
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

    private val mMonitorEventListener = object : MonitorEventListener {
        override fun onSyncStart() {
        }

        override fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int) {
            tv_progress_0.text = resources.getString(R.string.sync_progress_0, packageNumber)
            tv_progress_1.text = resources.getString(R.string.sync_progress_1, progress)
        }

        override fun onSyncSuccess() {
            dismissOldCreateNewSumianImageTextDialog().show(SumianImageTextDialog.TYPE_SUCCESS, resources.getString(R.string.already_latest_data), 0, SumianImageTextDialog.SHOW_DURATION_SHORT)
        }

        override fun onSyncFailed() {
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
                    .setTitle(R.string.turn_on_pa_mode_failed_title)
                    .setMessage(resources.getString(R.string.turn_on_pa_mode_failed_desc, message))
                    .setRightBtn(R.string.haode, null)
                    .show()
        }
    }

    private fun dismissOldCreateNewSumianImageTextDialog(): SumianImageTextDialog {
        if (mSumianImageTextDialog != null) {
            mSumianImageTextDialog?.dismiss()
        }
        return SumianImageTextDialog(context)
    }

    private fun dismissSumianImageTextDialog() {
        if (mSumianImageTextDialog != null && mSumianImageTextDialog!!.isShowing) {
            mSumianImageTextDialog!!.dismiss()
        }
    }

}