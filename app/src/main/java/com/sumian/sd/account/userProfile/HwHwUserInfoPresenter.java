package com.sumian.sd.account.userProfile;

import android.text.TextUtils;

import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;

import java.lang.ref.WeakReference;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class HwHwUserInfoPresenter implements HwUserInfoContract.Presenter, HwUserInfoCallback {

    private WeakReference<HwUserInfoContract.View> mViewWeakReference;

    private HwHwUserInfoPresenter(HwUserInfoContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static void init(HwUserInfoContract.View view) {
        new HwHwUserInfoPresenter(view);
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
        WeakReference<HwUserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        HwUserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onStartSyncUserInfo();
    }

    @Override
    public void onSyncUserInfoSuccess(UserInfo userInfo) {
        WeakReference<HwUserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        HwUserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onSyncCacheUserInfoSuccess(userInfo);
    }

    @Override
    public void onSyncUserInfoFailed(String error) {
        WeakReference<HwUserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        HwUserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onSyncCacheUserInfoFailed(error);
    }

    @Override
    public void onCompletedUserInfo() {
        WeakReference<HwUserInfoContract.View> viewWeakReference = this.mViewWeakReference;
        HwUserInfoContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onCompletedUserInfo();
    }
}
