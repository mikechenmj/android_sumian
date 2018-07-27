package com.sumian.app.account.presenter;

import android.support.annotation.Nullable;

import com.sumian.app.account.contract.LoginContract;
import com.sumian.app.account.service.SyncUserInfoService;
import com.sumian.app.app.AppManager;
import com.sumian.app.network.api.SleepyApi;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.request.LoginBody;
import com.sumian.app.network.response.Token;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private WeakReference<LoginContract.View> mViewWeakReference;
    private List<Call> mCalls;

    private LoginPresenter(LoginContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mCalls = new ArrayList<>();
    }

    public static void init(LoginContract.View view) {
        new LoginPresenter(view);
    }

    @Override
    public void doLogin(LoginBody loginBody) {

        LoginContract.View view = checkView();
        if (view == null) return;

        SleepyApi sleepyApi = AppManager
            .getNetEngine()
            .getHttpService();
        if (sleepyApi == null) return;

        view.onBegin();

        Call<Token> call = sleepyApi.doLogin(loginBody);
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                AppManager.getAccountModel().updateTokenCache(response);
                view.loginSuccess();
                SyncUserInfoService.startService(SyncUserInfoService.SUMIAN_LOGIN_TYPE);
            }

            @Override
            protected void onFailure(String error) {
                view.loginFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public void release() {
        List<Call> calls = this.mCalls;
        for (Call call : calls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

    @Nullable
    private LoginContract.View checkView() {
        WeakReference<LoginContract.View> viewWeakReference = this.mViewWeakReference;
        LoginContract.View view = viewWeakReference.get();
        if (view == null) return null;
        return view;
    }

}
