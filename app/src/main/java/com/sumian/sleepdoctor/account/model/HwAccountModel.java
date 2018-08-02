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

    public String accessToken() {
        Token token = this.mToken;
        return token == null ? null : token.token;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public Token getToken() {
        return mToken;
    }

    public void updateTokenCache(Token token) {
        this.mToken = token;
        if (token == null) {
            return;
        }
        this.mUserInfo = token.user;
        HwAccountCache.updateTokenCache(token);
    }

    public void updateUserCache(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    public void login(boolean isOnlySync, int loginType) {
        if (!isOnlySync) {
            LeanCloudHelper.loginLeanCloud();
            LeanCloudHelper.registerPushService();
        }
    }

    public void bindSocialCache(Social social) {
        UserInfo userInfo = this.mUserInfo;
        List<Social> socialites = this.mUserInfo.getSocialites();
        if (socialites == null) {
            socialites = new ArrayList<>();
        }

        if (socialites.isEmpty()) {
            socialites.add(social);
        } else {
            for (int i = 0; i < socialites.size(); i++) {
                int type = socialites.get(i).getType();
                if (type == social.getType()) {
                    socialites.set(i, social);
                    break;
                }

            }
        }
        this.mUserInfo.setSocialites(socialites);
        updateUserCache(userInfo);
    }

    public void unbindOpenPlatform(int socialType) {
        UserInfo userInfo = this.mUserInfo;
        List<Social> socialites = this.mUserInfo.getSocialites();
        if (socialites == null || socialites.isEmpty()) {
            return;
        }

        for (int i = 0; i < socialites.size(); i++) {
            int type = socialites.get(i).getType();
            if (type == socialType) {
                socialites.remove(i);
                break;
            }
        }
        this.mUserInfo.setSocialites(socialites);
        updateUserCache(userInfo);
    }
}
