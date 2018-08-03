package com.sumian.hw.account.presenter;

import com.sumian.hw.account.contract.ResetPwdContract;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.ResetPwdBody;
import com.sumian.sleepdoctor.app.HwAppManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:
 */

public class RestPwdPresenter implements ResetPwdContract.Presenter {


    private static final String TAG = RestPwdPresenter.class.getSimpleName();

    private WeakReference<ResetPwdContract.View> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private List<Call> mCalls;

    private RestPwdPresenter(ResetPwdContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(HwAppManager
            .getHwNetEngine()
            .getHttpService());
        this.mCalls = new ArrayList<>();
    }

    public static void init(ResetPwdContract.View view) {
        new RestPwdPresenter(view);
    }

    @Override
    public void doResetPwd(ResetPwdBody resetPwdBody) {
        WeakReference<ResetPwdContract.View> viewWeakReference = this.mViewWeakReference;
        ResetPwdContract.View view = viewWeakReference.get();
        if (view == null) return;

        SleepyApi sleepyApi = mApiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();

        Call<Object> call = sleepyApi.doResetPwd(resetPwdBody);

        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {
                view.onResetPwdSuccess();
            }

            @Override
            protected void onFailure(String error) {
                view.onResetPwdFailed(error);
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
        for (Call call : mCalls) {
            boolean canceled = call.isCanceled();
            if (!canceled) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }
}
