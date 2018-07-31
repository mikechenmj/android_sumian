package com.sumian.hw.upgrade.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.upgrade.bean.VersionInfo;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public interface VersionContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncMonitorCallback(VersionInfo versionInfo);

        void onSyncSleepyCallback(VersionInfo versionInfo);

        void onSyncAppVersionCallback(AppUpgradeInfo versionInfo);

    }

    interface Presenter extends BasePresenter {

        void syncMonitorVersionInfo();

        void syncAppVersionInfo();

    }
}
