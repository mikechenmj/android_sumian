package com.sumian.sleepdoctor.account.model;

import android.text.TextUtils;

import com.sumian.blue.manager.BlueManager;
import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.account.callback.OnLogoutCallback;
import com.sumian.hw.account.callback.UserInfoCallback;
import com.sumian.hw.account.service.SyncUserInfoService;
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

    private List<UserInfoCallback> mUserInfoCallbacks;

    private OnLogoutCallback mOnLogoutCallback;

    public HwAccountModel() {
        this.mToken = HwAccountCache.getTokenCache(Token.class);
        this.mUserInfo = HwAccountCache.getUserCache(UserInfo.class);
    }

    public void addOnLogoutCallback(OnLogoutCallback onLogoutCallback) {
        this.mOnLogoutCallback = onLogoutCallback;
    }

    public void addOnSyncUserInfoCallback(UserInfoCallback userInfoCallback) {
        if (mUserInfoCallbacks == null) {
            mUserInfoCallbacks = new ArrayList<>();
        }
        if (mUserInfoCallbacks.contains(userInfoCallback)) {
            return;
        }
        mUserInfoCallbacks.add(userInfoCallback);
    }

    public void removeOnSyncUserInfoCallback(UserInfoCallback userInfoCallback) {
        List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
        if (userInfoCallbacks == null) {
            return;
        }
        userInfoCallbacks.remove(userInfoCallback);
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
        if (userInfo != null) {
            HwAccountCache.updateUserCache(userInfo);

            List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
            if (userInfoCallbacks == null || userInfoCallbacks.isEmpty()) {
                return;
            }

            for (UserInfoCallback userInfoCallback : userInfoCallbacks) {
                userInfoCallback.onSyncUserInfoSuccess(userInfo);
            }
        }
    }

    public void login(boolean isOnlySync, int loginType) {
        if (!isOnlySync) {
            if (loginType == SyncUserInfoService.OPEN_LOGIN_TYPE) {
                HwAppManager.getOpenAnalytics().onProfileSignIn("wechat", String.valueOf(mUserInfo.getId()));
            } else {
                HwAppManager.getOpenAnalytics().onProfileSignIn(String.valueOf(mUserInfo.getId()));
            }

            //注册 leancloud
            LeanCloudHelper.loginLeanCloud();
            LeanCloudHelper.registerPushService();
        }
    }


    public void logout() {
        this.mUserInfo = null;
        this.mToken = null;
        HwAppManager.getOpenAnalytics().onProfileSignOff();
        AppOperator.runOnThread(() -> {
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // ActivityManager activityManager = (ActivityManager) BaseApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
            // if (activityManager != null) {
            //     activityManager.clearApplicationUserData();
            //  }
            // } else {
            ReminderManager.updateReminder(null);
            HwAccountCache.clearCache();
            SumianConfig.clear();
            BluePeripheralCache.clear();
            BlueManager.init().doStopScan();
            // }
        });
        OnLogoutCallback onLogoutCallback = this.mOnLogoutCallback;
        if (onLogoutCallback == null) {
            return;
        }
        onLogoutCallback.onLogoutSuccess();
    }

    public void startUpdateUserCache() {
        List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
        if (userInfoCallbacks == null || userInfoCallbacks.isEmpty()) {
            return;
        }

        for (UserInfoCallback userInfoCallback : userInfoCallbacks) {
            userInfoCallback.onStartSyncUserInfo();
        }
    }

    public void updateUserCacheFailed(String error) {
        List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
        if (userInfoCallbacks == null || userInfoCallbacks.isEmpty()) {
            return;
        }

        for (UserInfoCallback userInfoCallback : userInfoCallbacks) {
            userInfoCallback.onSyncUserInfoFailed(error);
        }
    }

    public void updateUserCacheCompleted() {
        List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
        if (userInfoCallbacks == null || userInfoCallbacks.isEmpty()) {
            return;
        }

        for (UserInfoCallback userInfoCallback : userInfoCallbacks) {
            userInfoCallback.onCompletedUserInfo();
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
