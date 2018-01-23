package com.sumian.sleepdoctor.tab.presenter;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.tab.contract.GroupContract;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.BaseResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class GroupPresenter implements GroupContract.Presenter {

    private GroupContract.View mView;

    private int mNextPage;
    private static final int PER_PAGE = 15;

    private Call<BaseResponse<List<GroupDetail<UserProfile, UserProfile>>>> mCall;

    private GroupPresenter(GroupContract.View view) {
        view.bindPresenter(this);
        mView = view;
    }

    public static void init(GroupContract.View view) {
        new GroupPresenter(view);
    }

    @Override
    public void getGroups() {
        getGroups(1);
    }

    private void getGroups(int currentPage) {
        if (mView == null) return;

        mView.onBegin();

        Map<String, Integer> map = new HashMap<>();
        map.put("page", currentPage);
        map.put("per_page", PER_PAGE);

        mCall = AppManager.getHttpService().getGroups(map);
        mCall.enqueue(new BaseResponseCallback<BaseResponse<List<GroupDetail<UserProfile, UserProfile>>>>() {
            @Override
            protected void onSuccess(BaseResponse<List<GroupDetail<UserProfile, UserProfile>>> response) {
                List<GroupDetail<UserProfile, UserProfile>> data = response.data;

                if (data == null) {
                    if (currentPage == 1) {//第一次请求完全就没有数据
                        mView.onNoHaveAnyGroups();
                        return;
                    } else {
                        mView.noNoHaveMoreGroups(App.Companion.getAppContext().getString(R.string.no_have_more_data));//没有更多数据
                        return;
                    }
                }

                BaseResponse.Pagination meta = response.meta;
                if (meta != null) {
                    int tempCurrentPage = meta.current_page;
                    if (meta.total_page - tempCurrentPage > 0) {//说明有下一页
                        mNextPage = tempCurrentPage + 1;
                    } else {//说明没有下一页,即没有更多数据
                        if (currentPage != 1) {
                            mView.noNoHaveMoreGroups(App.Companion.getAppContext().getString(R.string.no_have_more_data));
                            return;
                        }
                    }
                }

                mView.onGetGroupsSuccess(data);
                AppManager.getGroupViewModel().notifyGroups(data);
            }

            @Override
            protected void onFailure(String error) {
                mView.onFailure(error);
                List<GroupDetail<UserProfile, UserProfile>> groupDetails = AppManager.getGroupViewModel().getGroupsLiveData().getValue();
                if (groupDetails == null || !groupDetails.isEmpty()) {
                    mView.onShowErrorGroupView();
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    @Override
    public void getNextGroups() {
        getGroups(mNextPage);
    }

    @Override
    public void release() {
        if (mCall == null) {
            return;
        }

        if (mCall.isCanceled()) {
            mCall = null;
            return;
        }

        mCall.cancel();
        mCall = null;
    }
}
