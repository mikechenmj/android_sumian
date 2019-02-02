package com.sumian.sd.buz.account.userProfile;

import com.sumian.common.base.BaseViewModel;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.account.bean.UserInfo;
import com.sumian.sd.common.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/18.
 * desc:
 */

public class ImproveUserProfilePresenter extends BaseViewModel implements ImproveUserProfileContract.Presenter {

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
    public void improveUserProfile(@NonNull String improveKey, @NonNull String newUserProfile) {

        if (mView == null) return;
        mView.onBegin();

        Map<String, String> map = new HashMap<>(1);

        map.put(improveKey, newUserProfile);

        Call<UserInfo> call = AppManager
                .getSdHttpService()
                .modifyUserProfile(map);

        this.mCall = call;
        call.enqueue(new BaseSdResponseCallback<UserInfo>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                mView.onFailure(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
                mView.onImproveUserProfileSuccess();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }


    public void onCleared() {
        if (mCall == null) {
            return;
        }
        if (mCall.isExecuted()) {
            mCall.cancel();
        }
    }
}
