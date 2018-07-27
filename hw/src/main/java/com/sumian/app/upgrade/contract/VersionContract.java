package com.sumian.app.upgrade.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.AppUpgradeInfo;
import com.sumian.app.upgrade.bean.VersionInfo;

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
