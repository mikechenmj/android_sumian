package com.sumian.hw.device.contract;

import android.support.annotation.StringRes;

import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.base.HwBaseView;
import com.sumian.hw.device.bean.BlueDevice;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc:
 */

public interface DeviceContract {

    interface View extends HwBaseView<Presenter> {

        void onMonitorCallback(BlueDevice monitor);

        void onUnbindCallback(BlueDevice monitor);

        void showStartingPaMode();

        void showTurnOnPaModeSuccess();

        void showTurnOnPaModeFailed(@StringRes int errorId);

        void syncDeviceSleepChaSuccess();

        void syncDeviceSleepChaFailed();

        void onEnableAdapterCallback();

        void onDisableAdapterCallback();
    }


    interface Presenter extends HwBasePresenter {

        boolean adapterIsEnable();

        BlueDevice checkCache();

        void doConnect(BlueDevice monitor);

        void doScan2Connect(BlueDevice monitor);

        void turnOnSleep();

        void doSyncSleepData();

        void doUnbind();

        void turnOnMonitoringMode(int monitoring_mode);

        BlueDevice getCurrentMonitor();

        boolean isConnected();
    }
}
