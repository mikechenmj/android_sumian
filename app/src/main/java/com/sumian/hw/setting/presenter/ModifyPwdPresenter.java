package com.sumian.hw.setting.presenter;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.setting.contract.ModifyPwdContract;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.network.request.ModifyPwdBody;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public class ModifyPwdPresenter implements ModifyPwdContract.Presenter {

    private WeakReference<ModifyPwdContract.View> mViewWeakReference;
    private List<Call> mCalls;

    private ModifyPwdPresenter(ModifyPwdContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mCalls = new ArrayList<>();
    }

    public static void init(ModifyPwdContract.View view) {
        new ModifyPwdPresenter(view);
    }


    @Override
    public void doResetPwd(ModifyPwdBody modifyPwdBody) {
        WeakReference<ModifyPwdContract.View> viewWeakReference = this.mViewWeakReference;
        viewWeakReference.get().onBegin();
        BaseSdResponseCallback<UserInfo> callback = new BaseSdResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
                ToastUtils.showShort(R.string.modify_success);
                ModifyPwdContract.View view = viewWeakReference.get();
                if (view != null) {
                    view.onModifyPwdSuccess();
                }
            }

            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                super.onFailure(errorResponse);
                ToastUtils.showShort(errorResponse.getMessage());
                ModifyPwdContract.View view = viewWeakReference.get();
                if (view != null) {
                    view.onModifyPwdFailed(errorResponse.getMessage());
                }
            }

        };
        Call<UserInfo> call;
        String oldPassword = modifyPwdBody.getOld_password();
        if (TextUtils.isEmpty(oldPassword)) {
            call = AppManager.getSdHttpService().modifyPasswordWithoutOldPassword(modifyPwdBody.getPassword(), modifyPwdBody.getPassword());
        } else {
            call = AppManager.getSdHttpService().modifyPassword(oldPassword, modifyPwdBody.getPassword(), modifyPwdBody.getPassword());
        }
        mCalls.add(call);
        call.enqueue(callback);
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
}
