package com.sumian.hw.account.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.sumian.hw.account.contract.OpenLoginContract;
import com.sumian.hw.account.service.SyncUserInfoService;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.account.bean.Token;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/12/16.
 * desc:
 */

public class OpenLoginPresenter implements OpenLoginContract.Presenter {


    private WeakReference<OpenLoginContract.View> mViewWeakReference;
    private SleepyApi mSleepyApi;
    private String mOpenUserInfo;

    private OpenLoginPresenter(OpenLoginContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mSleepyApi = HwAppManager.getNetEngine().getHttpService();
    }


    public static void init(OpenLoginContract.View view) {
        new OpenLoginPresenter(view);
    }

    @Override
    public void release() {

    }


    @Override
    public void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener) {
        OpenLoginContract.View view = mViewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onBegin();
        switch (shareMedia) {
            case WEIXIN:
                HwAppManager.getOpenLogin().weChatLogin(activity, authListener);
                break;
        }
    }

    @Override
    public void bindOpen(SHARE_MEDIA shareMedia, Map<String, String> OpenMap) {
        OpenMap.put("nickname", OpenMap.get("screen_name"));
        this.mOpenUserInfo = JSON.toJSONString(OpenMap);
        OpenLoginContract.View view = mViewWeakReference.get();
        if (view == null) {
            return;
        }

        view.onBegin();

        int openType = 0;
        switch (shareMedia) {
            case WEIXIN:
                openType = 0;
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", openType);
        map.put("union_id", OpenMap.get("unionid"));
        Call<Token> call = this.mSleepyApi.loginOpenPlatform(map);

        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                HwAppManager.getAccountModel().updateTokenCache(response);
                view.onBindOpenSuccess(response);
                SyncUserInfoService.startService(SyncUserInfoService.OPEN_LOGIN_TYPE);
            }

            @Override
            protected void onFailure(String error) {
                view.onFailure(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }

            @Override
            protected void onNotFound(String error) {
                super.onNotFound(error);
                view.onNotBindCallback(error, mOpenUserInfo);
            }
        });

    }
}
