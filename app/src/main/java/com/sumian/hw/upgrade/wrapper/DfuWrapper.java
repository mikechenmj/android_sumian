package com.sumian.hw.upgrade.wrapper;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sumian.blue.callback.BlueScanCallback;
import com.sumian.hw.log.LogManager;
import com.sumian.sd.app.AppManager;

public class DfuWrapper implements BlueScanCallback, Handler.Callback {

    private static final long SCAN_TIME_OUT_MILLISECOND = 30 * 1000L;

    private static final int MSG_WHAT_STOP_SCAN = 0x01;

    private Handler mWorkHandler;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Runnable mScan2ConnectCallback;
    private Runnable mScanFailedCallback;
    private String mDfuMac;

    public DfuWrapper() {
        this.mWorkHandler = new Handler(AppManager.getBlueManager().getWorkThread().getLooper(), this);
    }

    public void scan2Connect(String dfuMac, Runnable scan2ConnectCallback, Runnable scanFailedCallback) {
        this.mDfuMac = dfuMac;
        this.mScan2ConnectCallback = scan2ConnectCallback;
        this.mScanFailedCallback = scanFailedCallback;
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        AppManager.getBlueManager().addBlueScanCallback(this);
        AppManager.getBlueManager().doScan();
        mWorkHandler.sendEmptyMessageDelayed(MSG_WHAT_STOP_SCAN, SCAN_TIME_OUT_MILLISECOND);
    }

    @Override
    public void onBeginScanCallback() {
        LogManager.appendUserOperationLog("dfu升级,正在通过 mac 地址搜寻匹配的dfu 模式蓝牙设备");
    }

    @Override
    public void onLeScanCallback(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getAddress().equals(mDfuMac)) {
            mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
            stopScanAndUnRegisterScanCallback();
            mScan2ConnectCallback.run();
            LogManager.appendUserOperationLog("dfu升级,通过 dfu mac 地址搜寻到匹配的dfu蓝牙设备  name=" + device.getName() + "  mac=" + device.getAddress());
        }
    }

    @Override
    public void onFinishScanCallback() {
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        LogManager.appendUserOperationLog("dfu升级,通过 dfu mac 地址搜寻到匹配的蓝牙设备 扫描已安全停止");
    }

    public void release() {
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        stopScanAndUnRegisterScanCallback();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_STOP_SCAN:
                mMainHandler.post(() -> mScanFailedCallback.run());
                stopScanAndUnRegisterScanCallback();
                break;
            default:
                break;
        }
        return false;
    }

    private void stopScanAndUnRegisterScanCallback() {
        AppManager.getBlueManager().removeBlueScanCallback(this);
        AppManager.getBlueManager().doStopScan();
    }
}
