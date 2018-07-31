package com.sumian.hw.account.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.request.LoginBody;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface LoginContract {

    interface View extends BaseNetView<Presenter> {

        void loginSuccess();

        void loginFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doLogin(LoginBody loginBody);
    }
}
