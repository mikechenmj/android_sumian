package com.sumian.app.setting.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.sumian.app.app.AppManager;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.HwUserInfo;
import com.sumian.app.network.response.UserSetting;
import com.sumian.app.setting.contract.SettingContract;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public class SettingPresenter implements SettingContract.Presenter {

    private WeakReference<SettingContract.View> mViewWeakReference;
    private List<Call> mCalls;

    private SettingPresenter(SettingContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mCalls = new ArrayList<>();
    }


    public static void init(SettingContract.View view) {
        new SettingPresenter(view);
    }


    @Override
    public void release() {
        for (Call call : mCalls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.mCalls = null;
        this.mViewWeakReference = null;
    }

    @Override
    public void syncSleepDiary() {

        WeakReference<SettingContract.View> viewWeakReference = this.mViewWeakReference;
        SettingContract.View view = viewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        Call<UserSetting> call = AppManager.getNetEngine().getHttpService().syncUserSetting();
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<UserSetting>() {
            @Override
            protected void onSuccess(UserSetting response) {
                view.syncSleepDiaryCallback(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

    }

    @Override
    public void updateSleepDiary(int sleepDiaryEnable) {

        WeakReference<SettingContract.View> viewWeakReference = this.mViewWeakReference;
        SettingContract.View view = viewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        Call<UserSetting> call = AppManager.getNetEngine().getHttpService().updateUserSetting(sleepDiaryEnable);
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<UserSetting>() {
            @Override
            protected void onSuccess(UserSetting response) {
                view.syncSleepDiaryCallback(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener) {
        SettingContract.View view = mViewWeakReference.get();
        if (view == null) return;
        view.onBegin();
        switch (shareMedia) {
            case WEIXIN:
                AppManager.getOpenLogin().weChatLogin(activity, authListener);
                break;
        }
    }

    @Override
    public void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> openMap) {

        SettingContract.View view = mViewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        int openType = SocialPresenter.SOCIAL_WECHAT;
        switch (shareMedia) {
            case WEIXIN:
                openType = SocialPresenter.SOCIAL_WECHAT;
                break;
        }

        openMap.put("nickname", openMap.get("screen_name"));
        String openUserInfo = JSON.toJSONString(openMap);

        Call<HwUserInfo.Social> call = AppManager.getNetEngine().getHttpService().bindOpenPlatform(openType, openUserInfo);
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<HwUserInfo.Social>() {
            @Override
            protected void onSuccess(HwUserInfo.Social response) {
                AppManager.getAccountModel().bindSocialCache(response);
                view.onBindOpenSuccess(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onBindOpenFailed(error);
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }

        });

    }
}
