package com.sumian.hw.account.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.hw.network.response.HwToken;
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
