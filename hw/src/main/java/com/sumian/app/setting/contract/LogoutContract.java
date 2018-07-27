package com.sumian.app.setting.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public interface LogoutContract {

    interface View extends BaseNetView<Presenter> {

        void onLogoutSuccess();

        void onLogoutFailed(String error);
    }

    interface Presenter extends BasePresenter {

        void doLogout();

    }
}
