package com.sumian.sleepdoctor.account.presenter;

import android.util.Log;

import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.contract.LoginContract;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import kotlin.Unit;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginContract.View mView;

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
                        Log.e(TAG, "onSuccess: ---------->" + response);
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
}
