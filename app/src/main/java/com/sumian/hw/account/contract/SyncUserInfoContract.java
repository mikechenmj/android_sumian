package com.sumian.hw.account.contract;

import com.sumian.hw.base.BasePresenter;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:同步用户信息数据
 */

public interface SyncUserInfoContract {

    interface Presenter extends BasePresenter {

        void doSyncUserInfo(boolean isOnlySync,int loginType);
    }
}
