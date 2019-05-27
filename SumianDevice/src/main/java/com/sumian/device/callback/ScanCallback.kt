package com.sumian.device.callback

import android.bluetooth.BluetoothDevice

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 18:22
 * desc   :
 * version: 1.0
 */
interface ScanCallback {

    fun onStart(success: Boolean)

    fun onDeviceFound(bluetoothDevice: BluetoothDevice)

    fun onStop()
}