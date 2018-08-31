package com.sumian.sd.account.login;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;

/**
 * Created by jzz
 * on 2017/10/25.
 * desc:
 */

public interface ModifyUserInfoContract {

    String KEY_NICKNAME = "nickname";
    String KEY_GENDER = "gender";
    String KEY_BIRTHDAY = "birthday";
    String KEY_AREA = "area";
    String KEY_HEIGHT = "height";
    String KEY_WEIGHT = "weight";
    String KEY_CAREER="career";


    interface View<T> extends HwBaseNetView<Presenter> {

        void onModifySuccess(T t);

        void onModifyFailed(String error);

    }

    interface Presenter extends HwBasePresenter {

        void doModifyUserInfo(String formKey, Object formValue);

    }
}
