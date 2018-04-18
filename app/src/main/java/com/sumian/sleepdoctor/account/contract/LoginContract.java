package com.sumian.sleepdoctor.account.contract;

import android.app.Activity;

import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void onLoginSuccess(boolean isNewAccount);

        void onSendCaptchaSuccess();

        void onBindOpenSuccess(Token token);

        void onNotBindCallback(String error, String openUserInfo);
    }


    interface Presenter extends BasePresenter {

        void doLogin(String mobile, String captcha);

        void doSendCaptcha(String mobile);

        void doLoginOpen(SHARE_MEDIA shareMedia, Activity activity, UMAuthListener authListener);

        void checkOpenIsBind(SHARE_MEDIA shareMedia, Map<String, String> OpenMap);

    }
}
