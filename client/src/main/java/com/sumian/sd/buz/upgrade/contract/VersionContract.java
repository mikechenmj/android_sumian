package com.sumian.sd.buz.upgrade.contract;

import com.sumian.sd.base.HwBaseNetView;
import com.sumian.sd.buz.upgrade.bean.VersionInfo;
import com.sumian.sd.buz.upgrade.presenter.DeviceVersionNoticeViewModel;
import com.sumian.sd.common.network.response.AppUpgradeInfo;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public interface VersionContract {

    interface View extends HwBaseNetView<DeviceVersionNoticeViewModel> {

        void onSyncMonitorCallback(VersionInfo versionInfo);

        void onSyncSleepyCallback(VersionInfo versionInfo);

        void onSyncAppVersionCallback(AppUpgradeInfo versionInfo);

    }


}
