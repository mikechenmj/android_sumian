package com.sumian.app.improve.device.model;

import android.text.TextUtils;

import com.sumian.app.account.model.AccountModel;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.improve.device.bean.BlueDevice;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.HwUserInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by jzz
 * on 2017/11/24.
 * desc:负责Device相关业务逻辑，例如维护Device的status，读写Device
 */

@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class DeviceModel {

    @SuppressWarnings("unused")
    private static final String TAG = DeviceModel.class.getSimpleName();

    public static final int MONITOR_IDLE_SNOOPING_STATE = 0x00;

    public static final int MONITOR_SNOOPING_STATE = 0x01;
    public static final int SLEEPY_IDLE_PA_STATE = 0x00;//0ff state

    private int mMonitorBattery;
    private int mSleepyBattery;
    private int mSleepyConnectState;
    private String mMonitorSn;
    private String mSleepySn;
    private String mMonitorVersion;
    private String mSleepyVersion;
    private String mSleepyMac;
    private int mMonitorSnoopingModeState;
    private int mSleepyPaModeState;
    private BlueDevice mBlueDevice;
    private Set<OnDeviceStatusChangeListener> mOnDeviceStatusChangeListeners = new HashSet<>();
    private Set<SyncSleepDataListener> mSyncSleepDataListeners = new HashSet<>();

    public int getMonitorBattery() {
        return mMonitorBattery;
    }

    public DeviceModel setMonitorBattery(int monitorBattery) {
        mMonitorBattery = monitorBattery;
        return this;
    }

    public int getSleepyBattery() {
        return mSleepyBattery;
    }

    public DeviceModel setSleepyBattery(int sleepyBattery) {
        mSleepyBattery = sleepyBattery;
        return this;
    }

    public int getSleepyConnectState() {
        return mSleepyConnectState;
    }

    public DeviceModel setSleepyConnectState(int sleepyConnectState) {
        mSleepyConnectState = sleepyConnectState;
        return this;
    }

    public boolean sleepyIsConnected() {
        return mSleepyConnectState == 0x01;
    }

    public String getMonitorSn() {
        return mMonitorSn;
    }

    public DeviceModel setMonitorSn(String monitorSn) {
        mMonitorSn = monitorSn;
        uploadBindSn(mSleepySn, monitorSn);
        return this;
    }

    public String getSleepySn() {
        return mSleepySn;
    }

    public DeviceModel setSleepySn(String sleepySn) {
        mSleepySn = sleepySn;
        uploadBindSn(sleepySn, this.mMonitorSn);
        return this;
    }

    public String getMonitorVersion() {
        return mMonitorVersion;
    }

    public DeviceModel setMonitorVersion(String monitorVersion) {
        mMonitorVersion = monitorVersion;
        return this;
    }

    public String getSleepyVersion() {
        return mSleepyVersion;
    }

    public DeviceModel setSleepyVersion(String sleepyVersion) {
        mSleepyVersion = sleepyVersion;
        return this;
    }

    public String getSleepyMac() {
        return mSleepyMac;
    }

    public DeviceModel setSleepyMac(String sleepyMac) {
        mSleepyMac = sleepyMac;
        return this;
    }

    private void uploadBindSn(String sleepySn, String monitorSn) {
        Map<String, Object> map = new HashMap<>();

        AccountModel accountModel = HwAppManager.getAccountModel();

        HwUserInfo userInfo = accountModel.getUserInfo();
        userInfo.setSleeper_sn(sleepySn);
        userInfo.setMonitor_sn(monitorSn);

        if (!TextUtils.isEmpty(userInfo.getMonitor_sn())) {
            map.put("monitor_sn", userInfo.getMonitor_sn());
        }

        if (!TextUtils.isEmpty(userInfo.getSleeper_sn())) {
            map.put("sleeper_sn", userInfo.getSleeper_sn());
        }

        HwAppManager.getNetEngine().getHttpService().doModifyUserInfo(map).enqueue(new BaseResponseCallback<HwUserInfo>() {
            @Override
            protected void onSuccess(HwUserInfo response) {
                HwAppManager.getAccountModel().updateUserCache(response);
            }

            @Override
            protected void onFailure(String error) {
                //uploadBindSn(sleepySn, monitorSn);
            }
        });
    }

    public String getSleepyDfuMac() {
        //CD9DC408D89D
        String sleepyMac = this.mSleepyMac;

        //由于 dfu 升级需要设备 mac+1

        //uint64 x old mac;, y new mac;
        // y = (( x & 0xFF ) + 1) + ((x >> 8) << 8);
        long oldMac = Long.parseLong(sleepyMac, 16);
        long newMac = ((oldMac & 0xff) + 1) + ((oldMac >> 8) << 8);

        StringBuilder macSb = new StringBuilder();
        //  macSb.delete(0, macSb.length());

        String hexString = Long.toHexString(newMac);
        for (int i = 0, len = hexString.length(); i < len; i++) {
            if (i % 2 == 0) {
                macSb.append(hexString.substring(i, i + 2));
                if (i != len - 2) {
                    macSb.append(":");
                }
            }
        }
        return macSb.toString().toUpperCase(Locale.getDefault());
    }

    public void rollbackAll() {
        this.mMonitorBattery = 0x00;
        this.mSleepyBattery = 0x00;
        this.mSleepyConnectState = 0x00;
        this.mMonitorSn = null;
        this.mSleepySn = null;
        this.mMonitorVersion = null;
        this.mSleepyVersion = null;
        this.mSleepyMac = null;
        this.mMonitorSnoopingModeState = MONITOR_IDLE_SNOOPING_STATE;
        this.mSleepyPaModeState = SLEEPY_IDLE_PA_STATE;
    }

    public int getMonitorSnoopingModeState() {
        return mMonitorSnoopingModeState;
    }

    public void setMonitorSnoopingModeState(int monitorSnoopingModeState) {
        this.mMonitorSnoopingModeState = monitorSnoopingModeState;
    }

    public int getSleepyPaModeState() {
        return mSleepyPaModeState;
    }

    public void setSleepyPaModeState(int sleepyPaModeState) {
        mSleepyPaModeState = sleepyPaModeState;
    }

    public interface OnLoadSleepDataCallback {
        void onLoadCallback(boolean isSync);
    }

    public BlueDevice getBlueDevice() {
        return mBlueDevice;
    }

    public void updateBlueDeviceAndNotifyListeners(BlueDevice blueDevice) {
        this.mBlueDevice = blueDevice;
        for (OnDeviceStatusChangeListener listener : mOnDeviceStatusChangeListeners) {
            listener.onDeviceStatusChange(blueDevice);
        }
    }

    public void updateSyncSleepDataProgressAndNotifyListeners(int packageNumber, int currentPosition, int total) {
        for (SyncSleepDataListener listener : mSyncSleepDataListeners) {
            listener.onSyncProgressChange(packageNumber, currentPosition, total);
        }
    }

    public void notifyStartSyncSleepData() {
        for (SyncSleepDataListener listener : mSyncSleepDataListeners) {
            listener.onSyncStart();
        }
    }

    public void notifyFinishSyncSleepData() {
        for (SyncSleepDataListener listener : mSyncSleepDataListeners) {
            listener.onSyncFinish();
        }
    }

    public void registerOnDeviceStatusChangeListener(OnDeviceStatusChangeListener listener) {
        mOnDeviceStatusChangeListeners.add(listener);
    }

    public void unregisterOnDeviceStatusChangeListener(OnDeviceStatusChangeListener listener) {
        mOnDeviceStatusChangeListeners.remove(listener);
    }

    public void registerOnSyncSleepDataProgressChangeListener(SyncSleepDataListener listener) {
        mSyncSleepDataListeners.add(listener);
    }

    public void unregisterOnSyncSleepDataProgressChangeListener(SyncSleepDataListener listener) {
        mSyncSleepDataListeners.remove(listener);
    }

    public interface OnDeviceStatusChangeListener {
        void onDeviceStatusChange(BlueDevice blueDevice);
    }

    public interface SyncSleepDataListener {
        /**
         * @param packageNumber   第几个数据包
         * @param currentPosition 同步进度
         * @param total           当前包数据总量
         */
        void onSyncProgressChange(int packageNumber, int currentPosition, int total);

        void onSyncStart();

        void onSyncFinish();
    }
}
