package com.sumian.sd.buz.upgrade.contract;

import android.content.Context;

import com.sumian.sd.base.HwBaseNetView;
import com.sumian.sd.base.HwBasePresenter;
import com.sumian.sd.buz.upgrade.bean.VersionInfo;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:
 */

public interface VersionUpgradeContract {

    interface View extends HwBaseNetView<Presenter> {

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

    interface Presenter extends HwBasePresenter {

        void downloadFile(int versionType, VersionInfo versionInfo);

        void upgrade(int versionType);

        void showDfuProgressNotification(Context context);

        void abort();

    }
}
