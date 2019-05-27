package com.sumian.devicedemo.device

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.data.DeviceVersionInfo
import com.sumian.device.manager.DeviceManager
import com.sumian.device.util.VersionUtil
import com.sumian.devicedemo.R
import kotlinx.android.synthetic.main.activity_device_version.*
import kotlinx.android.synthetic.main.layout_label_content_item.view.*

class DeviceVersionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_version)

        val device = DeviceManager.getDevice()
        item_monitor.tv_label.text = "监测仪升级"
        item_monitor.tv_content.text = device?.monitorSn
        item_monitor.setOnClickListener { launchUpgradeActivity(UpgradeDeviceActivity.TYPE_MONITOR) }
        item_sleep_master.tv_label.text = "速眠仪升级"
        item_sleep_master.tv_content.text = device?.sleepMasterSn
        item_sleep_master.setOnClickListener { launchUpgradeActivity(UpgradeDeviceActivity.TYPE_SLEEP_MASTER) }
    }

    override fun onStart() {
        super.onStart()
        queryData()
    }

    private fun queryData() {
        val device = DeviceManager.getDevice()
        if (device == null) {
            item_monitor?.iv_red_dot?.isVisible = false
            item_sleep_master?.iv_red_dot?.isVisible = false
            return
        }
        DeviceManager.getLatestVersionInfo(object : AsyncCallback<DeviceVersionInfo> {
            override fun onSuccess(data: DeviceVersionInfo?) {
                if (data == null) return
                item_monitor?.iv_red_dot?.isVisible =
                        DeviceManager.isMonitorConnected() && VersionUtil.hasNewVersion(
                                data.monitor?.version,
                                device.monitorVersionInfo?.softwareVersion
                        )
                item_sleep_master?.iv_red_dot?.isVisible =
                        DeviceManager.isSleepMasterConnected() && VersionUtil.hasNewVersion(
                                data.sleeper?.version,
                                device.sleepMasterVersionInfo?.softwareVersion
                        )
            }

            override fun onFail(code: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
    }

    private fun launchUpgradeActivity(type: Int) {
        UpgradeDeviceActivity.launch(this, type = type)
    }
}
