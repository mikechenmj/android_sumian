package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.LoginBody;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface LoginContract {

    interface View extends HwBaseNetView<Presenter> {

        void loginSuccess();

        void loginFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doLogin(LoginBody loginBody);
    }
}
