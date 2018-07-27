package com.sumian.app.account.callback;

import com.sumian.app.network.response.UserInfo;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public interface UserInfoCallback {

    void onStartSyncUserInfo();

    void onSyncUserInfoSuccess(UserInfo userInfo);

    void onSyncUserInfoFailed(String error);

    void onCompletedUserInfo();


}
