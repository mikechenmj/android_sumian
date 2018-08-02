package com.sumian.hw.account.presenter;

import com.sumian.hw.account.contract.OpenBindContract;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.app.AppManager;
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
        if (view == null) {
            return;
        }

        view.onBegin();

        SleepyApi sleepyApi = HwAppManager.getNetEngine().getHttpService();

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

        Call<Token> call = sleepyApi.bindOpenPlatform(map);
        this.mCalls.add(call);

        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                AppManager.getAccountViewModel().updateToken(response);
                view.onBindSuccess(response);
                AppManager.getAccountViewModel().updateToken(response);
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
        if (view == null) {
            return;
        }

        view.onBegin();

        SleepyApi sleepyApi = HwAppManager.getNetEngine().getHttpService();

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
