package com.sumian.devicedemo.device

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.manager.DeviceManager
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseActivity
import com.sumian.devicedemo.develop.DevelopActivity
import com.sumian.devicedemo.sleepdata.SleepDataActivity
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    companion object {
        const val REQUEST_CODE_SLEEP_MASTER_SN = 1000
        const val REQUEST_CODE_REQUEST_PERMISSION = 1001
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermission()
        v_develop_entrance.setOnClickListener {
            startActivity(Intent(this, DevelopActivity::class.java))
        }
        bt_upgrade_device.setOnClickListener {
            startActivity(
                    Intent(
                            this,
                            DeviceVersionActivity::class.java
                    )
            )
        }
        bt_change_sleep_master.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("确定更换绑定吗？")
                    .setMessage("此功能适用于监测仪或速眠仪发生故障，更换设备后重新绑定速眠仪的操作，是否继续？")
                    .setPositiveButton(
                            R.string.confirm
                    ) { _, _ ->
                        startActivityForResult(
                                Intent(this@MainActivity, CaptureActivity::class.java),
                                REQUEST_CODE_SLEEP_MASTER_SN
                        )
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
        bt_unbind.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("确定解除绑定吗？")
                    .setMessage("解绑后将断开监测仪与手机的链接")
                    .setPositiveButton(
                            R.string.confirm
                    ) { _, _ -> DeviceManager.unbind() }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
        bt_sleep_data.setOnClickListener {
            startActivity(Intent(this, SleepDataActivity::class.java))
        }
        bt_sync_pattern.setOnClickListener { DeviceManager.syncPattern() }
        updateButtonEnable()
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
        DeviceManager.tryToConnectBoundDevice(object : ConnectDeviceCallback {
            override fun onStart() {
            }

            override fun onSuccess() {
            }

            override fun onFail(code: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
    }

    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String) {
            updateButtonEnable()
        }
    }

    override fun onDestroy() {
        DeviceManager.unregisterDeviceStatusListener(mDeviceStatusListener)
        System.exit(0)
        super.onDestroy()
    }

    private fun updateButtonEnable() {
        val monitorConnected = DeviceManager.isMonitorConnected()
        bt_upgrade_device.isEnabled = monitorConnected
        bt_change_sleep_master.isEnabled = monitorConnected
        bt_unbind.isEnabled = DeviceManager.hasBoundDevice()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE_SLEEP_MASTER_SN == requestCode) {
            onScanSleepMasterQrCode(data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onScanSleepMasterQrCode(data: Intent?) {
        if (null != data) {
            val sn = data.getStringExtra(Constant.CODED_CONTENT)
            AlertDialog.Builder(this)
                    .setTitle("确定绑定这台速眠仪吗？")
                    .setMessage("速眠仪版本号： $data")
                    .setPositiveButton(
                            R.string.confirm
                    ) { _, _ ->
                        bindSleepMaster(sn)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()

        }
    }

    private fun bindSleepMaster(sn: String?) {
        DeviceManager.changeSleepMaster(sn, object : AsyncCallback<Any> {
            override fun onSuccess(data: Any?) {
                ToastUtils.showShort("绑定成功")
            }

            override fun onFail(code: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = PermissionUtils.getPermissions()
            requestPermissions(
                    permissions.toTypedArray(),
                    REQUEST_CODE_REQUEST_PERMISSION
            )
        }
    }

}
