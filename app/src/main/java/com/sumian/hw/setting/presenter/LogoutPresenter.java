package com.sumian.hw.setting.presenter;

import com.avos.avoscloud.AVInstallation;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.setting.contract.LogoutContract;
import com.sumian.sd.account.login.OnLogoutCallback;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.utils.AppUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public class LogoutPresenter implements LogoutContract.Presenter, OnLogoutCallback {

    private WeakReference<LogoutContract.View> mViewWeakReference;
    private List<Call> mCalls;

    private LogoutPresenter(LogoutContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mCalls = new ArrayList<>();
    }

    public static void init(LogoutContract.View view) {
        new LogoutPresenter(view);
    }

    @Override
    public void doLogout() {
        WeakReference<LogoutContract.View> viewWeakReference = this.mViewWeakReference;
        LogoutContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onBegin();

        String deviceToken = AVInstallation.getCurrentInstallation().getInstallationId();
        Call<Object> call = AppManager.getHwHttpService().doLogout(deviceToken);

        call.enqueue(new BaseSdResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                AppUtil.logoutAndLaunchLoginActivity();
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                view.onLogoutFailed(errorResponse.getMessage());
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
        List<Call> calls = this.mCalls;
        for (Call call : calls) {
            if (call.isExecuted()) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

    @Override
    public void onLogoutSuccess() {
        WeakReference<LogoutContract.View> viewWeakReference = this.mViewWeakReference;
        LogoutContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.onLogoutSuccess();
    }
}
