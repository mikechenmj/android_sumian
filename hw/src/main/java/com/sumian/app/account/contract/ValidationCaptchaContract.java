package com.sumian.app.account.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.request.ValidationCaptchaBody;

/**
 * Created by jzz
 * on 2017/10/7
 * <p>
 * desc:忘记密码,获取凭证
 */

public interface ValidationCaptchaContract {

    interface View extends BaseNetView<Presenter> {

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

        void onValidationCaptchaSuccess(String ticket);

        void onValidationCaptchaFailed(String error);
    }


    interface Presenter extends BasePresenter {

        void doCaptcha(CaptchaBody captchaBody);

        void doValidationCaptcha(ValidationCaptchaBody validationCaptchaBody);

    }
}
