package com.sumian.sd.account.loginold;

import android.app.Activity;

import com.sumian.sd.account.bean.Token;
import com.sumian.sd.base.SdBasePresenter;
import com.sumian.sd.base.SdBaseView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public interface LoginContract {

    interface View extends SdBaseView<Presenter> {

        void onLoginSuccess(boolean isNewAccount);

        void onSendCaptchaSuccess();

        void onBindOpenSuccess(Token token);

        void onNotBindCallback(String error, String openUserInfo);
    }


    interface Presenter extends SdBasePresenter {

        void doLogin(String mobile, String captcha);

        void doSendCaptcha(String mobile);

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void checkOpenIsBind(SHARE_MEDIA shareMedia, Map<String, String> OpenMap);

    }
}
