package com.sumian.hw.account.contract;

import android.support.annotation.StringRes;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.HwUserInfo;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public interface UserInfoContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncCacheUserInfoSuccess(HwUserInfo userInfo);

        void onSyncCacheUserInfoFailed(String error);

        void onStartSyncUserInfo();

        void onCompletedUserInfo();

    }


    interface Presenter extends BasePresenter {

        void doLoadCacheUserInfo();

        void doRefreshUserInfo();

        @StringRes
        int formatGender(String gender);

    }
}
