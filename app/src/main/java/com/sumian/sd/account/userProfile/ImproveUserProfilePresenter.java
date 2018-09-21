package com.sumian.sd.account.userProfile;

import android.support.annotation.NonNull;

import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseResponseCallback;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/18.
 * desc:
 */

public class ImproveUserProfilePresenter implements ImproveUserProfileContract.Presenter {

    private ImproveUserProfileContract.View mView;
    private Call<UserInfo> mCall;

    private ImproveUserProfilePresenter(ImproveUserProfileContract.View view) {
        view.setPresenter(this);
        this.mView = view;
    }

    public static void init(ImproveUserProfileContract.View view) {
        new ImproveUserProfilePresenter(view);
    }

    @Override
    public void improveUserProfile(String improveKey, String newUserProfile) {

        if (mView == null) return;
        mView.onBegin();

        Map<String, String> map = new HashMap<>(1);

        map.put(improveKey, newUserProfile);

        Call<UserInfo> call = AppManager
                .getHttpService()
                .modifyUserProfile(map);

        this.mCall = call;
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
                mView.onImproveUserProfileSuccess();
            }

            @Override
            protected void onFailure(int code, @NonNull String message) {
                mView.onFailure(message);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }


    @Override
    public void release() {
        if (mCall == null) {
            return;
        }
        if (mCall.isExecuted()){
            mCall.cancel();
        }
    }
}
