package com.sumian.hw.account.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.RegisterBody;
import com.sumian.hw.network.response.HwToken;

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
