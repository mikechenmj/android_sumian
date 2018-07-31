package com.sumian.hw.upgrade.contract;

import android.content.Context;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.upgrade.bean.VersionInfo;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:
 */

public interface VersionUpgradeContract {

    interface View extends BaseNetView<Presenter> {

        void onDownloadStartCallback();

        void onDownloadProgress(int progress);

        void onDownloadFirmwareSuccess();

        void onDownloadFirmwareFailed(String error);

        void onCheckBluetoothAddressFailed();

        void onScanFailed(String deviceAddress);

    }

    interface Presenter extends BasePresenter {

        void downloadFile(int versionType, VersionInfo versionInfo);

        void upgrade(int versionType);

        void showDfuProgressNotification(Context context);

        void abort();

    }
}
