package com.sumian.app.setting.presenter;

import com.avos.avoscloud.AVInstallation;
import com.hyphenate.chat.ChatClient;
import com.sumian.app.account.callback.OnLogoutCallback;
import com.sumian.app.app.App;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.setting.contract.LogoutContract;
import com.sumian.app.utils.NotificationUtil;

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
        HwAppManager.getAccountModel().addOnLogoutCallback(this);
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
        Call<Object> call = HwAppManager.getNetEngine().getHttpService().doLogout(deviceToken);

        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                HwAppManager.getAccountModel().logout();
                ChatClient.getInstance().logout(true, null);
                NotificationUtil.Companion.cancelAllNotification(App.getAppContext());
            }

            @Override
            protected void onFailure(String error) {
                view.onLogoutFailed(error);
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
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

    @Override
    public void onLogoutSuccess() {
        WeakReference<LogoutContract.View> viewWeakReference = this.mViewWeakReference;
        LogoutContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onLogoutSuccess();
    }
}
