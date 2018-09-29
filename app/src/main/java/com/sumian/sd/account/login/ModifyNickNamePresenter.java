package com.sumian.sd.account.login;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;

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

    private WeakReference<ModifyUserInfoContract.View<UserInfo>> mViewWeakReference;
    private Call mCall;

    private ModifyNickNamePresenter(ModifyUserInfoContract.View<UserInfo> view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
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

        view.onBegin();

        Map<String, String> map = new HashMap<>();
        map.put(formKey, formValue.toString());
        map.put("include", "doctor");
        Call<UserInfo> call = AppManager.getSdHttpService().modifyUserProfile(map);
        call.enqueue(new BaseSdResponseCallback<UserInfo>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                view.onModifyFailed(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(UserInfo response) {
                view.onModifySuccess(response);
                AppManager.getAccountViewModel().updateUserInfo(response);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

        this.mCall = call;

    }
}
