package com.sumian.sddoctor.account.contract;


import com.sumian.sddoctor.base.BasePresenter;
import com.sumian.sddoctor.base.BaseView;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public interface LogoutContract {

    interface View extends BaseView {

        void onLogoutSuccess();

        void onLogoutFailed(String error);
    }

    interface Presenter extends BasePresenter {

        void doLogout();

    }
}
