package com.sumian.sd.account.login;

import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;

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
        this.mApiWeakReference = new WeakReference<>(AppManager
                .getHwNetEngine()
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
        if (call.isExecuted()) {
            call.cancel();
        }
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

        Map<String, String> map = new HashMap<>();
        map.put(formKey, formValue.toString());
        map.put("include", "doctor");
        Call<UserInfo> call = AppManager.getHttpService().modifyUserProfile(map);
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                view.onModifySuccess(response);
                AppManager.getAccountViewModel().updateUserInfo(response);
            }

            @Override
            protected void onFailure(int code, String error) {
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
