package com.sumian.devicedemo.develop.ui

import android.bluetooth.BluetoothDevice
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.AdapterHost
import com.sumian.devicedemo.base.BaseAdapter
import com.sumian.devicedemo.base.BaseViewHolder
import kotlinx.android.synthetic.main.list_item_scan_device.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 13:37
 * desc   :
 * version: 1.0
 */
class DeviceAdapter(host: AdapterHost<BluetoothDevice>) :
        BaseAdapter<BluetoothDevice>(host, R.layout.list_item_scan_device) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemView = holder.itemView
        val device = mData[position]
        itemView.setOnClickListener { mHost?.onItemClick(device) }
        itemView.tv_device_name.text = device.name
    }

    override fun allowDuplicateData(): Boolean {
        return false
    }
}

