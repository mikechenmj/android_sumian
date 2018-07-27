package com.sumian.app.account.contract;

import android.support.annotation.StringRes;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.UserInfo;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public interface UserInfoContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncCacheUserInfoSuccess(UserInfo userInfo);

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
