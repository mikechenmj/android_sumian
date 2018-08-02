package com.sumian.sleepdoctor.account.model;

import android.text.TextUtils;

import com.sumian.blue.manager.BlueManager;
import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.account.callback.OnLogoutCallback;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.common.cache.BluePeripheralCache;
import com.sumian.hw.common.config.SumianConfig;
import com.sumian.hw.common.operator.AppOperator;
import com.sumian.hw.leancloud.LeanCloudHelper;
import com.sumian.hw.reminder.ReminderManager;
import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:  个人账户信息管理模块
 */

public class HwAccountModel {

    private static final String TAG = HwAccountModel.class.getSimpleName();

    private volatile Token mToken;
    private volatile UserInfo mUserInfo;

    public HwAccountModel() {
        this.mToken = HwAccountCache.getTokenCache(Token.class);
        this.mUserInfo = HwAccountCache.getUserCache(UserInfo.class);
    }

    public boolean isLogin() {
        Token token = this.mToken;
        return token != null && !TextUtils.isEmpty(token.getToken());// && token.getExpired_at() * 1000L > System.currentTimeMillis();
    }

    public boolean isHaveUserInfoAndSleepBarrierTest() {
        return mUserInfo != null && mUserInfo.isHaveUserInfoAndSleepBarrierTest();
    }

    public boolean isHaveAnswers() {
        return mUserInfo != null && mUserInfo.isHaveAnswers();
    }

    public boolean isHaveFullUserInfo() {
        return mUserInfo != null && mUserInfo.isHaveFullUserInfo();
    }

    public String getLeanCloudId() {
        UserInfo userInfo = this.mUserInfo;
        return userInfo == null ? null : userInfo.leancloud_id;
    }

    public String getTokenString() {
        Token token = this.mToken;
        return token == null ? null : token.token;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public Token getToken() {
        return mToken;
    }

    public void updateToken(Token token) {
        this.mToken = token;
        if (token == null) {
            return;
        }
        this.mUserInfo = token.user;
        HwAccountCache.updateTokenCache(token);
    }

    public void updateUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }
}
