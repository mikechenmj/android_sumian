package com.sumian.hw.setting.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.ConfigInfo;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public interface ConfigContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncConfigInfoSuccess(List<ConfigInfo> configs);

        void onSyncConfigInfoFailed(String error);

        void onSyncUrl(String url);

    }


    interface Presenter extends HwBasePresenter {

        void doSyncConfigInfo(int configType);
    }
}
