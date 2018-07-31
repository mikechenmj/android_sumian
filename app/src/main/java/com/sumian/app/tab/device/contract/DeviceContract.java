package com.sumian.app.tab.device.contract;

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.Fragment;

import com.sumian.app.base.BasePresenter;
import com.sumian.app.base.BaseView;
import com.sumian.app.tab.device.bean.BlueDevice;
import com.sumian.blue.model.BluePeripheral;


/**
 * Created by jzz
 * on 2017/8/29
 * <p>
 * desc:
 */

public interface DeviceContract {

    interface View extends BaseView<Presenter> {

        void onNotifyAdapterTurnOnSuccess();

        void onNotifyAdapterTurnOff();

        void onScanCallback(BluetoothDevice device, int rssi);

        void onStartScanCallback();

        void onStartScanFailed();

        void onStopScanCallback();

        void onDeviceConnecting(BluePeripheral peripheral);

        void onDeviceConnectedSuccess(BluePeripheral peripheral);

        void onDeviceDisconnectedSuccess(BluePeripheral peripheral);

        void onDeviceConnectedFailed(BluePeripheral peripheral);

        void onBluePeripheralCacheCallback(BlueDevice peripheral);

        void onNoHaveCacheCallback();

        void onMonitorBatteryCallback(int battery);

        void onSleepyConnectedStateCallback(int state);

        void onSleepyBatteryCallback(int battery);

        void onUnbindCallback(BluePeripheral peripheral);

        void onSleepyPaModeErrorCallback(String error);

        void onMonitorSnoopingModelCallback(int snoopingModeState);

        void onMonitorSnoopingModeErrorCallback(String error);

        void onSleepyPaModeCallback(int paModeState);

    }

    interface Presenter extends BasePresenter {

        boolean isConnected();

        void enable(Fragment fragment);

        boolean isEnable();

        void scan();

        void stopScan();

        void connect(String name, String mac);

        void directConnect();

        void disconnect();

        void unBind();

        void syncBluePeripheralCache();

        void doSleepyPower(int turnOn);

        void doMonitorSnoopingMode(int turnOn);

        void doSleepyPaMode(int turnOn);

        boolean isScan();
    }

}
