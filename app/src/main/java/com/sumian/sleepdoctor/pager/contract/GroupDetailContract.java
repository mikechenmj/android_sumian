package com.sumian.sleepdoctor.pager.contract;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public interface GroupDetailContract {

    interface View extends BaseView<Presenter> {

        void onGetGroupDetailSuccess(GroupDetail<UserProfile, UserProfile> groupDetail);
    }


    interface Presenter extends BasePresenter {

        void getGroupDetail(int groupId);
    }
}
