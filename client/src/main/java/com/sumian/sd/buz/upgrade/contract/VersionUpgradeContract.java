package com.sumian.sd.buz.upgrade.contract;

import com.sumian.sd.base.HwBaseNetView;
import com.sumian.sd.buz.upgrade.presenter.DeviceVersionUpgradePresenter;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:
 */

public interface VersionUpgradeContract {

    interface View extends HwBaseNetView<DeviceVersionUpgradePresenter> {

        void onDownloadStartCallback();

        void onDownloadProgress(int progress);

        void onDownloadFirmwareSuccess();

        void onDownloadFirmwareFailed(String error);

        void onCheckBluetoothAddressFailed();

        void onScanFailed(String deviceAddress);

        void showSleepConnectingDialog();

        void dismissSleepConnectingDialog();

        void showUpgradeDialog();

    }

}
