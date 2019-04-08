package com.sumian.blue.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sumian.blue.callback.BlueAdapterCallback;
import com.sumian.blue.callback.BlueScanCallback;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.blue.util.BlueUtil;
import com.sumian.blue.util.ILog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/11/19.
 * <p>
 * desc:
 */

@SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
public class BlueManager {

    private static final String TAG = BlueManager.class.getSimpleName();
    private static final long STOP_SCAN_DELAY_MILLIS = 1000L * 17;
    private static volatile BlueManager INSTANCE = null;

    private BluetoothAdapter mBluetoothAdapter;
    private HandlerThread mHandlerThread;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private boolean mIsScanning;
    private volatile List<BlueAdapterCallback> mBlueAdapterCallbacks = new ArrayList<>(0);
    private volatile List<BlueScanCallback> mBlueScanCallbacks = new ArrayList<>(0);
    private BluePeripheral mBluePeripheral;
    private ILog mLog;
    private ScanForDeviceListener mScanForDeviceListener;
    private String mScanDeviceMac;

    private BlueManager() {
        mHandlerThread = new HandlerThread("blueManagerThread") {
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                Log.e(TAG, "onLooperPrepared: --------blueManager looper------>");
            }
        };
        mHandlerThread.start();
    }

    public static BlueManager getInstance() {
        if (INSTANCE == null) {
            synchronized (BlueManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BlueManager();
                }
            }
        }
        return INSTANCE;
    }

    public void with(Context context) {
        registerBluetoothReceiver(context);
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;
    }

    private Runnable mDelayStopScanRunnable = () -> stopScan(true);

    public void startScanAndAutoStopAfter(long delayTimes) {
        removeDelayStopScanRunnable();
        if (isEnable()) {
            log("蓝牙 startLeScan");
            clearBluePeripheral();
            mIsScanning = mBluetoothAdapter.startLeScan(mLeScanCallback);
            if (mIsScanning) {
                onScanStart();
                postDelayStopScanRunnable(delayTimes);
            }
        } else {
            log("Start scan failed because bluetooth is not enable");
        }
    }

    private void postDelayStopScanRunnable(long delayTimes) {
        mMainHandler.postDelayed(mDelayStopScanRunnable, delayTimes);
    }

    public void stopScan() {
        stopScan(false);
    }

    private void stopScan(boolean isTimeout) {
        removeDelayStopScanRunnable();
        Log.d(TAG, "蓝牙 stopLeScan");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mIsScanning = false;
        onScanStop(isTimeout);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void enable() {
        if (!isEnable()) {
            mBluetoothAdapter.enable();
        }
    }

    public void disable() {
        removeDelayStopScanRunnable();
        mIsScanning = false;
        mBluetoothAdapter.disable();
    }

    public void refresh() {
        if (mBluePeripheral != null) {
            BlueUtil.refresh(mBluePeripheral.getGatt());
        }
    }

    public boolean isEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isLeScanning() {
        return mIsScanning;
    }

    public void saveBluePeripheral(BluePeripheral bluePeripheral) {
        this.mBluePeripheral = bluePeripheral;
        refresh();
    }

    public BluePeripheral getBluePeripheral() {
        return mBluePeripheral;
    }

    public boolean isBluePeripheralConnected() {
        return mBluePeripheral != null && mBluePeripheral.isConnected();
    }

    public void clearBluePeripheral() {
        if (mBluePeripheral != null) {
            mBluePeripheral.close();
            mBluePeripheral = null;
        }
    }

    public void addBlueAdapterCallback(BlueAdapterCallback blueAdapterCallback) {
        synchronized (this) {
            boolean contains = mBlueAdapterCallbacks.contains(blueAdapterCallback);
            if (contains) return;
            mBlueAdapterCallbacks.add(blueAdapterCallback);
        }
    }

    public void removeBlueAdapterCallback(BlueAdapterCallback blueAdapterCallback) {
        synchronized (this) {
            if (mBlueAdapterCallbacks == null || mBlueAdapterCallbacks.isEmpty()) return;
            mBlueAdapterCallbacks.remove(blueAdapterCallback);
        }
    }

    public void addBlueScanCallback(BlueScanCallback blueScanCallback) {
        synchronized (this) {
            boolean contains = mBlueScanCallbacks.contains(blueScanCallback);
            if (contains) return;
            mBlueScanCallbacks.add(blueScanCallback);
        }
    }

    public void removeBlueScanCallback(BlueScanCallback blueScanCallback) {
        synchronized (this) {
            if (mBlueScanCallbacks == null || mBlueScanCallbacks.isEmpty()) return;
            mBlueScanCallbacks.remove(blueScanCallback);
        }
    }

    public HandlerThread getWorkThread() {
        return this.mHandlerThread;
    }

    public BluetoothDevice getBluetoothDeviceFromMac(String mac) {
        if (mBluetoothAdapter != null && checkBluetoothAddress(mac)) {
            return mBluetoothAdapter.getRemoteDevice(mac);
        } else {
            return null;
        }
    }

    private boolean checkBluetoothAddress(String address) {
        return BluetoothAdapter.checkBluetoothAddress(address);
    }

    public void release() {
        removeDelayStopScanRunnable();
        if (mBluePeripheral != null) {
            mBluePeripheral.release();
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device == null || TextUtils.isEmpty(device.getName())) {
                return;
            }
            if (device.getName().startsWith("M-SUMIAN")) {
                log("onLeScan " + device.getName());
            }
            for (BlueScanCallback scanCallback : mBlueScanCallbacks) {
                scanCallback.onLeScan(device, rssi, scanRecord);
            }
            if (mScanForDeviceListener != null && device.getAddress().equals(mScanDeviceMac)) {
                removeDelayStopScanRunnable();
                mScanForDeviceListener.onDeviceFound(device);
                mScanForDeviceListener = null;
                mScanDeviceMac = null;
                // 先移除 mScanForDeviceListener，再stop
                stopScan();
            }
        }
    };

    private void onScanStart() {
        log("onScanStart");
        for (BlueScanCallback blueScanCallback : mBlueScanCallbacks) {
            blueScanCallback.onScanStart();
        }
    }

    private void onScanStop(boolean isTimeout) {
        log("onScanStop: " + isTimeout);
        for (BlueScanCallback blueScanCallback : mBlueScanCallbacks) {
            blueScanCallback.onScanStop(isTimeout);
        }
        if (mScanForDeviceListener != null) {
            mScanForDeviceListener.onScanStop(isTimeout);
        }
    }

    private void registerBluetoothReceiver(Context context) {
        BroadcastReceiver blueReceiver = new BroadcastReceiver() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) return;
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:

                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                        for (BlueAdapterCallback blueAdapterCallback : mBlueAdapterCallbacks) {
                            blueAdapterCallback.onAdapterStateCallback(state);
                        }

                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                Log.e(TAG, "onReceive: ------state off--->");

                                removeDelayStopScanRunnable();
                                if (mIsScanning) {
                                    stopScan();
                                }
                                closeBluePeripheral();

                                for (BlueAdapterCallback blueAdapterCallback : mBlueAdapterCallbacks) {
                                    blueAdapterCallback.onAdapterDisable();
                                }

                                break;
                            case BluetoothAdapter.STATE_ON:
                                removeDelayStopScanRunnable();
                                Log.e(TAG, "onReceive: ------state on--->");

                                for (BlueAdapterCallback blueAdapterCallback : mBlueAdapterCallbacks) {
                                    blueAdapterCallback.onAdapterEnable();
                                }

                                break;
                            default:
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        closeBluePeripheral();
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        refresh();
                        break;
                    default:
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        context.registerReceiver(blueReceiver, filter);
    }

    private void closeBluePeripheral() {
        if (mBluePeripheral != null) {
            mBluePeripheral.close();
        }
    }

    private void removeDelayStopScanRunnable() {
        mMainHandler.removeCallbacks(mDelayStopScanRunnable);
    }

    /**
     * Use with @see #stopScanForDevice
     *
     * @param mac      device mac
     * @param listener callback
     */
    public void scanForDevice(String mac, ScanForDeviceListener listener) {
        mScanDeviceMac = mac;
        mScanForDeviceListener = listener;
        startScanAndAutoStopAfter(STOP_SCAN_DELAY_MILLIS);
    }

    public void stopScanForDevice() {
        mScanForDeviceListener = null;
        stopScan();
    }

    public void setLog(ILog log) {
        mLog = log;
    }

    private void log(String msg) {
        Log.d(TAG, msg);
        if (mLog != null) {
            mLog.log(msg);
        }
    }

    public interface ScanForDeviceListener {
        // 找到设备后，移除监听，不接受onScanStop事件
        void onDeviceFound(BluetoothDevice device);

        // 超时和在搜索过程中调用stopScan()都会执行到onScanStop()
        void onScanStop(boolean isTimeout);
    }
}
