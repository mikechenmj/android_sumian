package com.sumian.hw.setting.presenter;

import com.avos.avoscloud.AVInstallation;
import com.hyphenate.chat.ChatClient;
import com.sumian.blue.manager.BlueManager;
import com.sumian.common.operator.AppOperator;
import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.account.callback.OnLogoutCallback;
import com.sumian.hw.common.cache.BluePeripheralCache;
import com.sumian.hw.common.config.SumianConfig;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.reminder.ReminderManager;
import com.sumian.hw.setting.contract.LogoutContract;
import com.sumian.hw.utils.NotificationUtil;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;

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
        Call<Object> call = AppManager.getHwNetEngine().getHttpService().doLogout(deviceToken);

        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                AppManager.getOpenAnalytics().onProfileSignOff();
                AppOperator.runOnThread(() -> {
                    ReminderManager.updateReminder(null);
                    HwAccountCache.clearCache();
                    SumianConfig.clear();
                    BluePeripheralCache.clear();
                    BlueManager.init().doStopScan();
                });
                ChatClient.getInstance().logout(true, null);
                NotificationUtil.Companion.cancelAllNotification(App.Companion.getAppContext());
                AppManager.getAccountViewModel().updateToken(null);
            }

            @Override
            protected void onFailure(int code, String error) {
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
