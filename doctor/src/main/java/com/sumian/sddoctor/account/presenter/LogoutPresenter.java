package com.sumian.sddoctor.account.presenter;


import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.account.contract.LogoutContract;
import com.sumian.sddoctor.app.App;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public class LogoutPresenter implements LogoutContract.Presenter {

    private WeakReference<LogoutContract.View> mViewWeakReference;

    private LogoutPresenter(LogoutContract.View view) {
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static LogoutContract.Presenter init(LogoutContract.View view) {
        return new LogoutPresenter(view);
    }

    @Override
    public void doLogout() {
        WeakReference<LogoutContract.View> viewWeakReference = this.mViewWeakReference;
        LogoutContract.View view = viewWeakReference.get();
        if (view == null) {
            return;
        }
        view.showLoading();

        Call<Object> call = AppManager.getHttpService().logout();

        call.enqueue(new BaseSdResponseCallback<Object>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                view.onLogoutFailed(App.Companion.getAppContext().getString(R.string.logout_failed_please_check_network));
            }

            @Override
            protected void onSuccess(Object response) {
                AppManager.getAccountViewModel().logout();
                view.onLogoutSuccess();
            }

            @Override
            protected void onFinish() {
                view.dismissLoading();
            }
        });
    }
}
