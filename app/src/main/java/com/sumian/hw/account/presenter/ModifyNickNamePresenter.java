package com.sumian.hw.account.presenter;

import com.sumian.hw.account.contract.ModifyUserInfoContract;
import com.sumian.sleepdoctor.app.HwAppManager;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;

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

public class ModifyNickNamePresenter implements ModifyUserInfoContract.Presenter {

    private static final String TAG = ModifyNickNamePresenter.class.getSimpleName();

    private WeakReference<ModifyUserInfoContract.View<UserInfo>> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private Call mCall;

    private ModifyNickNamePresenter(ModifyUserInfoContract.View<UserInfo> view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(HwAppManager
            .getNetEngine()
            .getHttpService());
    }

    public static void init(ModifyUserInfoContract.View<UserInfo> view) {
        new ModifyNickNamePresenter(view);
    }

    @Override
    public void release() {
        Call call = this.mCall;
        if (call == null) {
            return;
        }
        if (call.isCanceled()) {
            return;
        }
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
        map.put("include", "doctor");
        Call<UserInfo> call = sleepyApi.doModifyUserInfo(map);
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                view.onModifySuccess(response);
                AppManager.getAccountViewModel().updateUserInfo(response);
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
