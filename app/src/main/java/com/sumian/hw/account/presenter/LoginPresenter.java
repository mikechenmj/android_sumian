package com.sumian.hw.account.presenter;

import android.support.annotation.Nullable;

import com.sumian.hw.account.contract.LoginContract;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.LoginBody;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.app.AppManager;

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
        if (view == null) {
            return;
        }

        SleepyApi sleepyApi = AppManager
                .getHwNetEngine()
                .getHttpService();
        if (sleepyApi == null) {
            return;
        }

        view.onBegin();

        Call<Token> call = sleepyApi.doLogin(loginBody);
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                AppManager.getAccountViewModel().updateToken(response);
                view.loginSuccess();
            }

            @Override
            protected void onFailure(int code, String error) {
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
        if (view == null) {
            return null;
        }
        return view;
    }

}
