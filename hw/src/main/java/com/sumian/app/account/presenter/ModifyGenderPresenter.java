package com.sumian.app.account.presenter;

import com.sumian.app.account.contract.ModifyUserInfoContract;
import com.sumian.app.app.AppManager;
import com.sumian.app.network.api.SleepyApi;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.UserInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/26.
 * <p>
 * desc:
 */

public class ModifyGenderPresenter implements ModifyUserInfoContract.Presenter {

    private static final String TAG = ModifyGenderPresenter.class.getSimpleName();

    private WeakReference<ModifyUserInfoContract.View<UserInfo>> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private Call mCall;

    private ModifyGenderPresenter(ModifyUserInfoContract.View<UserInfo> view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(AppManager
            .getNetEngine()
            .getHttpService());
    }

    public static void init(ModifyUserInfoContract.View<UserInfo> view) {
        new ModifyGenderPresenter(view);
    }

    @Override
    public void release() {
        Call call = this.mCall;
        if (call == null) return;
        if (call.isCanceled()) return;
        call.cancel();
    }

    @Override
    public void doModifyUserInfo(String formKey, Object formValue) {

        WeakReference<ModifyUserInfoContract.View<UserInfo>> viewWeakReference = this.mViewWeakReference;
        ModifyUserInfoContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;

        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();

        Map<String, Object> map = new HashMap<>();
        map.put(formKey, formValue);
        Call<UserInfo> call = sleepyApi.doModifyUserInfo(map);
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                view.onModifySuccess(response);
                AppManager.getAccountModel().updateUserCache(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onModifyFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

        this.mCall = call;

    }
}
