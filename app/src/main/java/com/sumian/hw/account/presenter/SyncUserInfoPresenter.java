package com.sumian.hw.account.presenter;

import com.sumian.hw.account.contract.SyncUserInfoContract;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.account.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public class SyncUserInfoPresenter implements SyncUserInfoContract.Presenter {

    public static final String TAG = SyncUserInfoPresenter.class.getSimpleName();

    private List<Call> mCalls;

    private SyncUserInfoPresenter() {
        this.mCalls = new ArrayList<>();
    }

    public static SyncUserInfoPresenter init() {
        return new SyncUserInfoPresenter();
    }


    @Override
    public void doSyncUserInfo(boolean isOnlySync, int loginType) {

        HwAppManager.getAccountModel().startUpdateUserCache();

        Call<UserInfo> call = HwAppManager
            .getNetEngine()
            .getHttpService().getUserInfo();

        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                HwAppManager.getAccountModel().updateUserCache(response);
                HwAppManager.getAccountModel().login(isOnlySync,loginType);
            }

            @Override
            protected void onFailure(String error) {
                HwAppManager.getAccountModel().updateUserCacheFailed(error);
            }

            @Override
            protected void onFinish() {
                HwAppManager.getAccountModel().updateUserCacheCompleted();
            }
        });

        this.mCalls.add(call);
    }

    @Override
    public void release() {
        List<Call> calls = this.mCalls;
        if (calls == null || calls.isEmpty()) {
            return;
        }
        for (Call call : calls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    }
}
