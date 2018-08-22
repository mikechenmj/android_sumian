package com.sumian.sd.account.userProfile;

import android.support.annotation.StringRes;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.sd.account.bean.UserInfo;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public interface HwUserInfoContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncCacheUserInfoSuccess(UserInfo userInfo);

        void onSyncCacheUserInfoFailed(String error);

        void onStartSyncUserInfo();

        void onCompletedUserInfo();

    }


    interface Presenter extends HwBasePresenter {

        //   void doLoadCacheUserInfo();

//        void doRefreshUserInfo();

        @StringRes
        int formatGender(String gender);

    }
}
