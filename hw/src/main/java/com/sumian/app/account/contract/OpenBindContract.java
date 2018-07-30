package com.sumian.app.account.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.response.HwToken;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface OpenBindContract {

    interface View extends BaseNetView<Presenter> {

        void onBindSuccess(HwToken token);

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doBind(String mobile, String pwd, String captcha, SHARE_MEDIA shareMedia, String openUserInfo);

        void doCaptcha(CaptchaBody captchaBody);

    }
}
