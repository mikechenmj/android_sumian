package com.sumian.app.account.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.request.LoginBody;

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
