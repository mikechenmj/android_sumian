package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.request.CaptchaBody;
import com.sumian.sd.account.bean.Token;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface OpenBindContract {

    interface View extends HwBaseNetView<Presenter> {

        void onBindSuccess(Token token);

        void onCaptchaSuccess();

        void onCaptchaFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doBind(String mobile, String pwd, String captcha, SHARE_MEDIA shareMedia, String openUserInfo);

        void doCaptcha(CaptchaBody captchaBody);

    }
}
