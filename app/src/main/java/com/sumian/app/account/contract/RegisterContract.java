package com.sumian.app.account.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.request.RegisterBody;
import com.sumian.app.network.response.HwToken;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface RegisterContract {

    interface View extends BaseNetView<Presenter> {

        void onRegisterSuccess(HwToken token);

        void onRegisterFailed(String error);

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doRegister(RegisterBody registerBody);

        void doCaptcha(CaptchaBody captchaBody);

    }
}
