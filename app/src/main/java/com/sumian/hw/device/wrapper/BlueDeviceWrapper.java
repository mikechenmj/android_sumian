package com.sumian.hw.device.wrapper;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sumian.blue.callback.BlueScanCallback;
import com.sumian.hw.device.bean.BlueDevice;
import com.sumian.hw.log.LogManager;
import com.sumian.sd.app.AppManager;

public class BlueDeviceWrapper implements BlueScanCallback, Handler.Callback {

    private static final long SCAN_TIME_OUT_MILLISECOND = 5 * 1000L;

    private static final int MSG_WHAT_STOP_SCAN = 0x01;

    private BlueDevice mMonitor;
    private Handler mWorkHandler;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Runnable mScan2ConnectCallback;
    private Runnable mScanFailedCallback;

    public BlueDeviceWrapper() {
        this.mWorkHandler = new Handler(AppManager.getBlueManager().getWorkThread().getLooper(), this);
    }

    public void scan2Connect(BlueDevice monitor, Runnable scan2ConnectCallback, Runnable scanFailedCallback) {
        this.mScan2ConnectCallback = scan2ConnectCallback;
        this.mScanFailedCallback = scanFailedCallback;
        this.mMonitor = monitor;
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        AppManager.getBlueManager().addBlueScanCallback(this);
        AppManager.getBlueManager().doScan();
        mWorkHandler.sendEmptyMessageDelayed(MSG_WHAT_STOP_SCAN, SCAN_TIME_OUT_MILLISECOND);
    }

    @Override
    public void onBeginScanCallback() {
        LogManager.appendUserOperationLog("蓝牙设备准备连接,正在通过 mac 地址搜寻匹配的蓝牙设备");
    }

    @Override
    public void onLeScanCallback(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName().startsWith("M-SUMIAN") && device.getAddress().equals(mMonitor.mac)) {
            mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
            stopScanAndUnRegisterScanCallback();
            mScan2ConnectCallback.run();
            LogManager.appendUserOperationLog("蓝牙设备准备连接,通过 mac 地址搜寻到匹配的蓝牙设备  name=" + device.getName() + "  mac=" + device.getAddress());
        }
    }

    @Override
    public void onFinishScanCallback() {
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        LogManager.appendUserOperationLog("蓝牙设备准备连接,通过 mac 地址搜寻到匹配的蓝牙设备 扫描已安全停止");
    }

    public void release() {
        mWorkHandler.removeMessages(MSG_WHAT_STOP_SCAN);
        stopScanAndUnRegisterScanCallback();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_STOP_SCAN:
                mMonitor.status = BlueDevice.STATUS_UNCONNECTED;
                mMonitor.battery = 0;
                mMonitor.speedSleeper.status = BlueDevice.STATUS_UNCONNECTED;
                mMonitor.speedSleeper.battery = 0;
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
