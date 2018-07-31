package com.sumian.hw.account.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;

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


    interface View<T> extends BaseNetView<Presenter> {

        void onModifySuccess(T t);

        void onModifyFailed(String error);

    }

    interface Presenter extends BasePresenter {

        void doModifyUserInfo(String formKey, Object formValue);

    }
}
