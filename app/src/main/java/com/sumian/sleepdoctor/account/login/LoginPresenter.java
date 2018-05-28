package com.sumian.sleepdoctor.account.login;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private String mOpenUserInfo;

    private LoginPresenter(LoginContract.View view) {
        view.bindPresenter(this);
        this.mView = view;
    }

    public static void init(LoginContract.View view) {
        new LoginPresenter(view);
    }

    @Override
    public void doLogin(String mobile, String captcha) {
        if (mView == null) return;

        mView.onBegin();

        AppManager.getHttpService()
                .login(mobile, captcha)
                .enqueue(new BaseResponseCallback<Token>() {

                    @Override
                    protected void onSuccess(Token response) {
                        AppManager.getAccountViewModel().updateToken(response);
                        mView.onLoginSuccess(response.is_new);
                    }

                    @Override
                    protected void onFailure(String error) {
                        mView.onFailure(error);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mView.onFinish();
                    }
                });
    }

    @Override
    public void doSendCaptcha(String mobile) {
        if (mView == null) return;

        mView.onBegin();

        AppManager.getHttpService()
                .getCaptcha(mobile)
                .enqueue(new BaseResponseCallback<Unit>() {

                    @Override
                    protected void onSuccess(Unit response) {
                        mView.onSendCaptchaSuccess();
                    }

                    @Override
                    protected void onFailure(String error) {
                        mView.onFailure(error);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mView.onFinish();
                    }
                });
    }

    @Override
    public void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener) {
        mView.onBegin();
        switch (shareMedia) {
            case WEIXIN:
                AppManager.getOpenLogin().weChatLogin(activity, authListener);
                break;
        }
    }

    @Override
    public void checkOpenIsBind(SHARE_MEDIA shareMedia, Map<String, String> OpenMap) {
        OpenMap.put("nickname", OpenMap.get("screen_name"));
        this.mOpenUserInfo = JSON.toJSONString(OpenMap);

        mView.onBegin();

        int openType = 0;
        switch (shareMedia) {
            case WEIXIN:
                openType = 0;
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", openType);
        map.put("union_id", OpenMap.get("unionid"));
        Call<Token> call = AppManager.getHttpService().loginOpenPlatform(map);

        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                AppManager.getAccountViewModel().updateToken(response);
                mView.onBindOpenSuccess(response);
            }

            @Override
            protected void onFailure(String error) {
                mView.onFailure(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }

            @Override
            protected void onNotFound(String error) {
                super.onNotFound(error);
                mView.onNotBindCallback(error, mOpenUserInfo);
            }
        });

    }
}
