package com.sumian.sleepdoctor.account.userProfile;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

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
    private Call<UserProfile> mCall;


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

        Call<UserProfile> call = AppManager
                .getHttpService()
                .modifyUserProfile(map);

        this.mCall = call;
        call.enqueue(new BaseResponseCallback<UserProfile>() {
            @Override
            protected void onSuccess(UserProfile response) {
                AppManager.getAccountViewModel().updateUserProfile(response);
                mView.onImproveUserProfileSuccess();
            }

            @Override
            protected void onFailure(String error) {
                mView.onFailure(error);
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
        if (mCall == null || mCall.isCanceled()) return;
        mCall.cancel();
    }
}
