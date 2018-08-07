package com.sumian.hw.setting.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public interface LogoutContract {

    interface View extends HwBaseNetView<Presenter> {

        void onLogoutSuccess();

        void onLogoutFailed(String error);
    }

    interface Presenter extends HwBasePresenter {

        void doLogout();

    }
}
