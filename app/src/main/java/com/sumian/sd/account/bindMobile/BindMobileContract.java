package com.sumian.sd.account.bindMobile;

import com.sumian.sd.account.bean.Token;
import com.sumian.sd.base.SdBasePresenter;
import com.sumian.sd.base.SdBaseView;


/**
 * Created by sm
 * on 2018/2/28.
 * desc:
 */

public interface BindMobileContract {

    interface View extends SdBaseView<Presenter> {

        void onSendCaptchaSuccess();

        void bindOpenSocialSuccess(Token response);
    }


    interface Presenter extends SdBasePresenter {

        void doSendCaptcha(String mobile);

        void bindOpenSocial(String mobile, String captcha, int socialType, String socialInfo);

    }
}
