package com.sumian.devicedemo.device

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumian.device.callback.ScanCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.AdapterHost
import com.sumian.devicedemo.base.BaseActivity
import com.sumian.devicedemo.base.BaseAdapter
import com.sumian.devicedemo.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_scan_device.*
import kotlinx.android.synthetic.main.list_item_device.view.*

class ScanDeviceActivity : BaseActivity() {

    companion object {
        private const val RESULT_KEY = "DEVICE_MAC"
        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.context, ScanDeviceActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun getDeviceMacFromIntent(intent: Intent): String? {
            return intent.getStringExtra(RESULT_KEY)
        }
    }

    private val mAdapter by lazy {
        DeviceAdapter(mAdapterHost)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rv_device.adapter = mAdapter
        rv_device.layoutManager = LinearLayoutManager(this)

        scanDevice()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scan_device
    }


    private val mAdapterHost = object : AdapterHost<BluetoothDevice> {
        override fun onItemClick(data: BluetoothDevice) {
            AlertDialog.Builder(this@ScanDeviceActivity)
                    .setMessage("确认绑定：${data.name}")
                    .setPositiveButton(
                            R.string.confirm
                    ) { _, _ ->
                        DeviceManager.stopScan()
                        val intent = Intent()
                        intent.putExtra(RESULT_KEY, data.address)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    override fun onStop() {
        super.onStop()
        DeviceManager.stopScan()
    }

    fun scanDevice() {
        DeviceManager.scanDelay(mScanCallback)
    }

    private val mScanCallback = object : ScanCallback {
        override fun onStart(success: Boolean) {
        }

        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
            val name = device.name
            if (name != null && name.startsWith(DeviceManager.SUMIAN_DEVICE_NAME_PREFIX)) {
                mAdapter.addData(device)
            }
        }

        override fun onStop() {
        }
    }

    class DeviceAdapter(host: AdapterHost<BluetoothDevice>) :
            BaseAdapter<BluetoothDevice>(host, R.layout.list_item_device) {
        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            val itemView = holder.itemView
            val data = mData[position]
            itemView.tv_device_name.text = data.address
            itemView.setOnClickListener { mHost?.onItemClick(data) }
        }
    }
}
