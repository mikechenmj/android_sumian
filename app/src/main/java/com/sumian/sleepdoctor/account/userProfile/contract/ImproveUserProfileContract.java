package com.sumian.sleepdoctor.account.userProfile.contract;

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
    String IMPROVE_CAREER_KEY = "career";
    String IMPROVE_BIRTHDAY_KEY = "birthday";
    String IMPROVE_HEIGHT_KEY = "height";
    String IMPROVE_WEIGHT_KEY = "weight";
    String IMPROVE_GENDER_KEY = "gender";
    String IMPROVE_EDUCATION_KEY = "education";


    interface View extends BaseView<Presenter> {

        void onImproveUserProfileSuccess();
    }

    interface Presenter extends BasePresenter {

        void improveUserProfile(String improveKey, String newUserProfile);
    }
}
