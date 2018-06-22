package com.sumian.sleepdoctor.pager.presenter;

import android.support.annotation.NonNull;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.pager.contract.GroupDetailContract;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public class GroupDetailPresenter implements GroupDetailContract.Presenter {

    private GroupDetailContract.View mView;


    private GroupDetailPresenter(GroupDetailContract.View view) {
        view.setPresenter(this);
        mView = view;
    }

    public static void init(GroupDetailContract.View view) {
        new GroupDetailPresenter(view);
    }

    @Override
    public void getGroupDetail(int groupId) {

        mView.onBegin();

        AppManager
                .getHttpService()
                .getGroupsDetail(groupId, "users,packages")
                .enqueue(new BaseResponseCallback<GroupDetail<UserProfile, UserProfile>>() {
                    @Override
                    protected void onSuccess(GroupDetail<UserProfile, UserProfile> response) {
                        AppManager.getGroupViewModel().notifyGroupDetail(response);
                        mView.onGetGroupDetailSuccess(response);
                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
                        mView.onFailure(errorResponse.getMessage());
                        if (errorResponse.getCode() == 404) {
                            mView.onFailure(errorResponse.getMessage());
                        }
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mView.onFinish();
                    }

                });

    }
}
