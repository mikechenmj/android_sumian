package com.sumian.blue.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public interface BlueScanCallback extends BluetoothAdapter.LeScanCallback {

    default void onScanStart() {
    }

    void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    default void onScanStop() {
    }

    void onScanTimeout();
}
