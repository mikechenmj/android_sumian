package com.sumian.sleepdoctor.pager.presenter;

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


    public GroupDetailPresenter(GroupDetailContract.View view) {
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
                    protected void onFailure(ErrorResponse errorResponse) {
                        mView.onFailure(errorResponse.message);
                        if (errorResponse.status_code == 404) {
                            mView.onFailure(errorResponse.message);
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
