package com.sumian.sd.device

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/15 16:19
 * desc   :
 * version: 1.0
 */

object BluetoothUtil {
    fun startActivityForOpenBluetooth(fragment: androidx.fragment.app.Fragment, requestCode: Int) {
        fragment.startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode)
    }
}