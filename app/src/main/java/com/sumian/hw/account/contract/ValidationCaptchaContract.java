package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.request.ValidationCaptchaBody;

/**
 * Created by jzz
 * on 2017/10/7
 * <p>
 * desc:忘记密码,获取凭证
 */

public interface ValidationCaptchaContract {

    interface View extends HwBaseNetView<Presenter> {

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

        void onValidationCaptchaSuccess(String ticket);

        void onValidationCaptchaFailed(String error);
    }


    interface Presenter extends HwBasePresenter {

        void doCaptcha(CaptchaBody captchaBody);

        void doValidationCaptcha(ValidationCaptchaBody validationCaptchaBody);

    }
}
