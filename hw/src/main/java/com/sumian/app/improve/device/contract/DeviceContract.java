package com.sumian.app.improve.device.contract;

import android.support.annotation.StringRes;

import com.sumian.app.base.BasePresenter;
import com.sumian.app.base.BaseView;
import com.sumian.app.improve.device.bean.BlueDevice;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc:
 */

public interface DeviceContract {

    interface View extends BaseView<Presenter> {

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


    interface Presenter extends BasePresenter {

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
