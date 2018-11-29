package com.sumian.hw.upgrade.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.upgrade.bean.VersionInfo;
import com.sumian.sd.network.response.AppUpgradeInfo;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public interface VersionContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncMonitorCallback(VersionInfo versionInfo);

        void onSyncSleepyCallback(VersionInfo versionInfo);

        void onSyncAppVersionCallback(AppUpgradeInfo versionInfo);

    }

    interface Presenter extends HwBasePresenter {

        void syncMonitorVersionInfo();

        void syncAppVersionInfo();

    }
}
