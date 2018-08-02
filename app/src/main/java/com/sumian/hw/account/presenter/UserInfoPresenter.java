package com.sumian.hw.account.presenter;

import android.text.TextUtils;

import com.sumian.hw.account.callback.UserInfoCallback;
import com.sumian.hw.account.contract.UserInfoContract;
import com.sumian.hw.app.HwAppManager;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;

import java.lang.ref.WeakReference;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class UserInfoPresenter implements UserInfoContract.Presenter, UserInfoCallback {

    private WeakReference<UserInfoContract.View> mViewWeakReference;

    private UserInfoPresenter(UserInfoContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static void init(UserInfoContract.View view) {
        new UserInfoPresenter(view);
    }

    @Override
    public int formatGender(String gender) {

        int genderId = R.string.gender_secrecy_hint;

        if (TextUtils.isEmpty(gender)) {
            return genderId;
        }

        switch (gender) {
            case "male":
                genderId = R.string.gender_male_hint;
                break;
            case "female":
                genderId = R.string.gender_female_hint;
                break;
            case "secrecy":
                //genderId = R.string.gender_secrecy_hint;
                //break;
            default:
                genderId = R.string.user_none_default_hint;
                break;
        }
        return genderId;
    }

    @Override
    public void release() {
    }

    @Override
    public void onStartSyncUserInfo() {
        WeakReference<UserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        UserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onStartSyncUserInfo();
    }

    @Override
    public void onSyncUserInfoSuccess(UserInfo userInfo) {
        WeakReference<UserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        UserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onSyncCacheUserInfoSuccess(userInfo);
    }

    @Override
    public void onSyncUserInfoFailed(String error) {
        WeakReference<UserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        UserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onSyncCacheUserInfoFailed(error);
    }

    @Override
    public void onCompletedUserInfo() {
        WeakReference<UserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        UserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onCompletedUserInfo();
    }
}
