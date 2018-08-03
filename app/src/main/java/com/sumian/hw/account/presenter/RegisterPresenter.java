package com.sumian.hw.account.presenter;

import android.support.annotation.Nullable;

import com.sumian.hw.account.contract.RegisterContract;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.RegisterBody;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.HwAppManager;

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

public class RegisterPresenter implements RegisterContract.Presenter {

    private static final String TAG = RegisterPresenter.class.getSimpleName();

    private WeakReference<RegisterContract.View> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private List<Call> mCalls;

    private RegisterPresenter(RegisterContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(HwAppManager
            .getHwNetEngine()
            .getHttpService());
        this.mCalls = new ArrayList<>();
    }

    public static void init(RegisterContract.View view) {
        new RegisterPresenter(view);
    }

    @Override
    public void doRegister(RegisterBody registerBody) {

        RegisterContract.View view = checkView();
        if (view == null) {
            return;
        }

        SleepyApi sleepyApi = checkApi();
        if (sleepyApi == null) {
            return;
        }

        view.onBegin();

        Call<Token> call = sleepyApi.doRegister(registerBody);
        this.mCalls.add(call);

        call.enqueue(new BaseResponseCallback<Token>() {
            @Override
            protected void onSuccess(Token response) {
                AppManager.getAccountViewModel().updateToken(response);
                view.onRegisterSuccess(response);
                AppManager.getAccountViewModel().updateToken(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onRegisterFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public void doCaptcha(CaptchaBody captchaBody) {

        RegisterContract.View view = checkView();
        if (view == null) {
            return;
        }

        SleepyApi sleepyApi = checkApi();
        if (sleepyApi == null) {
            return;
        }

        view.onBegin();

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
            boolean canceled = call.isCanceled();
            if (!canceled) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

    @Nullable
    private RegisterContract.View checkView() {
        WeakReference<RegisterContract.View> viewWeakReference = this.mViewWeakReference;
        RegisterContract.View view = viewWeakReference.get();
        if (view == null) {
            return null;
        }
        return view;
    }

    @Nullable
    private SleepyApi checkApi() {
        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) {
            return null;
        }
        return sleepyApi;
    }

}
