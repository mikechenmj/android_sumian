package com.sumian.sleepdoctor.account.contract;

import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void onLoginSuccess(boolean isNewAccount);

        void onSendCaptchaSuccess();
    }


    interface Presenter extends BasePresenter {

        void doLogin(String mobile, String captcha);

        void doSendCaptcha(String mobile);

    }
}
