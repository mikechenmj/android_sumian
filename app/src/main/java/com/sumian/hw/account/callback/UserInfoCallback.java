package com.sumian.hw.account.callback;

import com.sumian.sd.account.bean.UserInfo;

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
