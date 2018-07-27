package com.sumian.sleepdoctor.account.bindMobile;

import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;


/**
 * Created by sm
 * on 2018/2/28.
 * desc:
 */

public interface BindMobileContract {

    interface View extends BaseView<Presenter> {

        void onSendCaptchaSuccess();

        void bindOpenSocialSuccess(Token response);
    }


    interface Presenter extends BasePresenter {

        void doSendCaptcha(String mobile);

        void bindOpenSocial(String mobile, String captcha, int socialType, String socialInfo);

    }
}
