package com.sumian.sleepdoctor.tab.contract;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

import java.util.List;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public interface GroupContract {

    interface View extends BaseView<Presenter> {

        void onNoHaveAnyGroups();

        void onGetGroupsSuccess(List<GroupDetail<UserProfile, UserProfile>> groups);

        void noNoHaveMoreGroups(String noHaveMoreMsg);

        void onShowErrorGroupView();
    }

    interface Presenter extends BasePresenter {

        void getGroups();

        void getNextGroups();
    }
}
