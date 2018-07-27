package com.sumian.app.account.presenter;

import com.sumian.app.account.contract.SyncUserInfoContract;
import com.sumian.app.app.AppManager;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.Reminder;
import com.sumian.app.network.response.ResultResponse;
import com.sumian.app.network.response.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        AppManager.getAccountModel().startUpdateUserCache();

        Call<UserInfo> call = AppManager
            .getNetEngine()
            .getHttpService().syncUserInfo();

        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountModel().updateUserCache(response);
                AppManager.getAccountModel().login(isOnlySync,loginType);
            }

            @Override
            protected void onFailure(String error) {
                AppManager.getAccountModel().updateUserCacheFailed(error);
            }

            @Override
            protected void onFinish() {
                AppManager.getAccountModel().updateUserCacheCompleted();
            }
        });

        this.mCalls.add(call);
    }

    @Override
    public void doSyncReminder() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("per_page", 20);
        map.put("type", 1);

        Call<ResultResponse<Reminder>> call = AppManager
            .getNetEngine()
            .getHttpService().syncReminder(map);

        call.enqueue(new BaseResponseCallback<ResultResponse<Reminder>>() {
            @Override
            protected void onSuccess(ResultResponse<Reminder> response) {
                List<Reminder> data = response.getData();
                AppManager.getAccountModel().updateReminder(data == null || data.isEmpty() ? null : (data.get(0)));
            }

            @Override
            protected void onFailure(String error) {
            }

            @Override
            protected void onFinish() {
            }
        });
    }

    @Override
    public void release() {
        List<Call> calls = this.mCalls;
        if (calls == null || calls.isEmpty()) return;
        for (Call call : calls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    }
}
