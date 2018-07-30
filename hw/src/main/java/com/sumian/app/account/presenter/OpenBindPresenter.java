package com.sumian.app.account.presenter;

import com.sumian.app.account.contract.OpenBindContract;
import com.sumian.app.account.service.SyncUserInfoService;
import com.sumian.app.app.AppManager;
import com.sumian.app.network.api.SleepyApi;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.response.HwToken;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public class OpenBindPresenter implements OpenBindContract.Presenter {

    //private static final String TAG = OpenBindPresenter.class.getSimpleName();

    private WeakReference<OpenBindContract.View> mViewWeakReference;
    private List<Call> mCalls;

    private OpenBindPresenter(OpenBindContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mCalls = new ArrayList<>();
    }

    public static void init(OpenBindContract.View view) {
        new OpenBindPresenter(view);
    }

    @Override
    public void doBind(String mobile, String pwd, String captcha, SHARE_MEDIA shareMedia, String openUserInfo) {
        WeakReference<OpenBindContract.View> viewWeakReference = this.mViewWeakReference;
        OpenBindContract.View view = viewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        SleepyApi sleepyApi = AppManager.getNetEngine().getHttpService();

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", pwd);
        map.put("captcha", captcha);
        int openType = 0;
        switch (shareMedia) {
            case WEIXIN:
                openType = 0;
                break;
        }

        map.put("type", openType);
        map.put("info", openUserInfo);

        Call<HwToken> call = sleepyApi.bindOpenPlatform(map);
        this.mCalls.add(call);

        call.enqueue(new BaseResponseCallback<HwToken>() {
            @Override
            protected void onSuccess(HwToken response) {
                AppManager.getAccountModel().updateTokenCache(response);
                view.onBindSuccess(response);
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
        });
    }

    @Override
    public void doCaptcha(CaptchaBody captchaBody) {

        WeakReference<OpenBindContract.View> viewWeakReference = this.mViewWeakReference;
        OpenBindContract.View view = viewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        SleepyApi sleepyApi = AppManager.getNetEngine().getHttpService();

        Call<Object> call = sleepyApi.doCaptcha(captchaBody);

        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                view.onCaptchaSuccess();
            }

            @Override
            protected void onFailure(String error) {
                view.onCaptchaFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

        this.mCalls.add(call);

    }

    @Override
    public void release() {
        for (Call call : mCalls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

}
