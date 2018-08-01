package com.sumian.sleepdoctor.account.userProfile.presenter;

import android.support.annotation.NonNull;

import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.account.userProfile.contract.ImproveUserProfileContract;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;

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
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                mView.onFailure(errorResponse.getMessage());
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
        if (mCall == null || mCall.isExecuted()) return;
        mCall.cancel();
    }
}
