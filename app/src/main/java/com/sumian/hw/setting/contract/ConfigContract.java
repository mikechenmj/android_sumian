package com.sumian.hw.setting.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.ConfigInfo;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public interface ConfigContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncConfigInfoSuccess(List<ConfigInfo> configs);

        void onSyncConfigInfoFailed(String error);

        void onSyncUrl(String url);

    }


    interface Presenter extends BasePresenter {

        void doSyncConfigInfo(int configType);
    }
}
