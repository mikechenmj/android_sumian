package com.sumian.hw.account.presenter;

import android.support.annotation.Nullable;

import com.sumian.hw.account.contract.ValidationCaptchaContract;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.ValidationCaptchaBody;
import com.sumian.hw.network.response.Ticket;
import com.sumian.sleepdoctor.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/7
 * <p>
 * desc:
 */

public class ValidationCaptchaPresenter implements ValidationCaptchaContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private WeakReference<ValidationCaptchaContract.View> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private List<Call> mCalls;

    private ValidationCaptchaPresenter(ValidationCaptchaContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(AppManager
            .getHwNetEngine()
            .getHttpService());
        this.mCalls = new ArrayList<>();
    }

    public static void init(ValidationCaptchaContract.View view) {
        new ValidationCaptchaPresenter(view);
    }


    @Override
    public void doCaptcha(CaptchaBody captchaBody) {

        ValidationCaptchaContract.View view = checkView();
        if (view == null) return;

        SleepyApi sleepyApi = checkApi();
        if (sleepyApi == null) return;

        view.onBegin();

        Call<Object> call = sleepyApi.doCaptcha(captchaBody);
        this.mCalls.add(call);

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
    }

    @Override
    public void doValidationCaptcha(ValidationCaptchaBody validationCaptchaBody) {

        ValidationCaptchaContract.View view = checkView();
        if (view == null) return;

        SleepyApi sleepyApi = checkApi();
        if (sleepyApi == null) return;

        view.onBegin();

        Call<Ticket> call = sleepyApi.doValidationCaptcha(validationCaptchaBody);
        this.mCalls.add(call);

        call.enqueue(new BaseResponseCallback<Ticket>() {
            @Override
            protected void onSuccess(Ticket response) {
                view.onValidationCaptchaSuccess(response.getTicket());
            }

            @Override
            protected void onFailure(String error) {
                view.onValidationCaptchaFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

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
    private ValidationCaptchaContract.View checkView() {
        WeakReference<ValidationCaptchaContract.View> viewWeakReference = this.mViewWeakReference;
        ValidationCaptchaContract.View view = viewWeakReference.get();
        if (view == null) return null;
        return view;
    }

    @Nullable
    private SleepyApi checkApi() {
        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return null;
        return sleepyApi;
    }
}
