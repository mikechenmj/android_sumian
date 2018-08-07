package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.RegisterBody;
import com.sumian.sleepdoctor.account.bean.Token;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface RegisterContract {

    interface View extends HwBaseNetView<Presenter> {

        void onRegisterSuccess(Token token);

        void onRegisterFailed(String error);

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doRegister(RegisterBody registerBody);

        void doCaptcha(CaptchaBody captchaBody);

    }
}
