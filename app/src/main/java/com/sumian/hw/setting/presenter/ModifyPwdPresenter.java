package com.sumian.hw.setting.presenter;

import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.ModifyPwdBody;
import com.sumian.hw.setting.contract.ModifyPwdContract;
import com.sumian.sleepdoctor.app.AppManager;

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
    private WeakReference<SleepyApi> mApiWeakReference;
    private List<Call> mCalls;

    private ModifyPwdPresenter(ModifyPwdContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(AppManager
            .getHwNetEngine()
            .getHttpService());
        this.mCalls = new ArrayList<>();
    }

    public static void init(ModifyPwdContract.View view) {
        new ModifyPwdPresenter(view);
    }


    @Override
    public void doResetPwd(ModifyPwdBody modifyPwdBody) {

        WeakReference<ModifyPwdContract.View> viewWeakReference = this.mViewWeakReference;
        ModifyPwdContract.View view = viewWeakReference.get();
        if (view == null) return;
        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();
        Call<Object> call = sleepyApi.doModifyPwd(modifyPwdBody);
        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                HwAccountCache.clearCache();
                AppManager.getAccountViewModel().updateUserInfo(null);
                AppManager.getAccountViewModel().updateToken(null);
                view.onModifyPwdSuccess();
            }

            @Override
            protected void onFailure(String error) {
                view.onModifyPwdFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

        mCalls.add(call);
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
