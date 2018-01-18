package com.sumian.sleepdoctor.account.contract;

import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;

/**
 * Created by jzz
 * on 2018/1/18.
 * desc:
 */

public interface ImproveUserProfileContract {

    String IMPROVE_NICKNAME_KEY = "nickname";
    String IMPROVE_NAME_KEY = "name";

    interface View extends BaseView<Presenter> {

        void onImproveUserProfileSuccess();
    }

    interface Presenter extends BasePresenter {

        void improveUserProfile(String improveKey, String newUserProfile);
    }
}
