package com.sumian.app.account.model;

import android.text.TextUtils;

import com.sumian.app.account.cache.AccountCache;
import com.sumian.app.account.callback.OnLogoutCallback;
import com.sumian.app.account.callback.OnSleepReminderCallback;
import com.sumian.app.account.callback.UserInfoCallback;
import com.sumian.app.account.service.SyncUserInfoService;
import com.sumian.app.app.AppManager;
import com.sumian.app.common.cache.BluePeripheralCache;
import com.sumian.app.common.config.SumianConfig;
import com.sumian.app.common.operator.AppOperator;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.network.response.HwToken;
import com.sumian.app.network.response.HwUserInfo;
import com.sumian.app.network.response.Reminder;
import com.sumian.blue.manager.BlueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:  个人账户信息管理模块
 */

public class AccountModel {

    private static final String TAG = AccountModel.class.getSimpleName();

    private volatile HwToken mToken;
    private volatile HwUserInfo mUserInfo;

    private List<UserInfoCallback> mUserInfoCallbacks;
    private OnSleepReminderCallback mOnSleepReminderCallback;

    private OnLogoutCallback mOnLogoutCallback;

    public AccountModel() {
        this.mToken = AccountCache.getTokenCache(HwToken.class);
        this.mUserInfo = AccountCache.getUserCache(HwUserInfo.class);
    }

    public void addOnLogoutCallback(OnLogoutCallback onLogoutCallback) {
        this.mOnLogoutCallback = onLogoutCallback;
    }

    public void addOnSyncUserInfoCallback(UserInfoCallback userInfoCallback) {
        if (mUserInfoCallbacks == null) {
            mUserInfoCallbacks = new ArrayList<>();
        }
        if (mUserInfoCallbacks.contains(userInfoCallback)) return;
        mUserInfoCallbacks.add(userInfoCallback);
    }

    public void removeOnSyncUserInfoCallback(UserInfoCallback userInfoCallback) {
        List<UserInfoCallback> userInfoCallbacks = this.mUserInfoCallbacks;
        if (userInfoCallbacks == null) return;
        userInfoCallbacks.remove(userInfoCallback);
    }

    public void addOnReminderCallback(OnSleepReminderCallback onSleepReminderCallback) {
        this.mOnSleepReminderCallback = onSleepReminderCallback;
    }

    public void removeOnReminderCallback(OnSleepReminderCallback onSleepReminderCallback) {
        this.mOnSleepReminderCallback = onSleepReminderCallback;
    }

    public boolean isLogin() {
        HwToken token = this.mToken;
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
        HwUserInfo userInfo = this.mUserInfo;
        return userInfo == null ? null : userInfo.getLeancloud_id();
    }

    public String accessToken() {
        HwToken token = this.mToken;
        return token == null ? null : token.getToken();
    }

    public HwUserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public HwToken getToken() {
        return mToken;
    }

    public void updateTokenCache(HwToken token) {
        this.mToken = token;
        if (token == null) return;
        this.mUserInfo = token.getUserInfo();
        AccountCache.updateTokenCache(token);
    }

    public void updateUserCache(HwUserInfo userInfo) {
        this.mUserInfo = userInfo;
        if (userInfo != null) {
            AccountCache.updateUserCache(userInfo);

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
                AppManager.getOpenAnalytics().onProfileSignIn("wechat", String.valueOf(mUserInfo.getId()));
            } else {
                AppManager.getOpenAnalytics().onProfileSignIn(String.valueOf(mUserInfo.getId()));
            }

            //注册 leancloud
            LeanCloudHelper.loginLeanCloud();
            LeanCloudHelper.registerPushService();
        }
    }


    public void logout() {
        this.mUserInfo = null;
        this.mToken = null;
        AppManager.getOpenAnalytics().onProfileSignOff();
        AppOperator.runOnThread(() -> {
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // ActivityManager activityManager = (ActivityManager) BaseApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
            // if (activityManager != null) {
            //     activityManager.clearApplicationUserData();
            //  }
            // } else {
            updateReminder(null);
            AccountCache.clearCache();
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

    public void updateReminder(Reminder reminder) {
        OnSleepReminderCallback onSleepReminderCallback = this.mOnSleepReminderCallback;
        if (onSleepReminderCallback == null) {
            return;
        }
        onSleepReminderCallback.onSleepReminderChange(reminder);
        SumianConfig.updateReminder(reminder);
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

    public void bindSocialCache(HwUserInfo.Social social) {
        HwUserInfo userInfo = this.mUserInfo;
        List<HwUserInfo.Social> socialites = this.mUserInfo.getSocialites();
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
        HwUserInfo userInfo = this.mUserInfo;
        List<HwUserInfo.Social> socialites = this.mUserInfo.getSocialites();
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
