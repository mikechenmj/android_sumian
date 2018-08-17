package com.sumian.hw.setting.presenter;

import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.ModifyPwdBody;
import com.sumian.hw.setting.contract.ModifyPwdContract;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;

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
        BaseResponseCallback<UserInfo> callback = new BaseResponseCallback<UserInfo>() {
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
            protected void onFailure(int code, String message) {
                ToastUtils.showShort(message);
                ModifyPwdContract.View view = viewWeakReference.get();
                if (view != null) {
                    view.onModifyPwdFailed(message);
                }
            }
        };
        Call<UserInfo> call;
        String oldPassword = modifyPwdBody.getOld_password();
        if (TextUtils.isEmpty(oldPassword)) {
            call = AppManager.getHttpService().modifyPasswordWithoutOldPassword(modifyPwdBody.getPassword(), modifyPwdBody.getPassword());
        } else {
            call = AppManager.getHttpService().modifyPassword(oldPassword, modifyPwdBody.getPassword(), modifyPwdBody.getPassword());
        }
        mCalls.add(call);
        call.enqueue(callback);
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
}
