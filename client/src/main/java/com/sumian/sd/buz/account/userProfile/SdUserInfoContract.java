package com.sumian.sd.buz.account.userProfile;

import android.app.Activity;

import com.sumian.sd.base.SdBasePresenter;
import com.sumian.sd.base.SdBaseView;
import com.sumian.sd.buz.account.bean.Social;
import com.sumian.sd.buz.account.bean.UserInfo;
import com.umeng.socialize.UMAuthListener;

/**
 * <pre>
 *     @author : sm
 *
 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/7/3 11:00
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
public interface SdUserInfoContract {

    interface View extends SdBaseView<Presenter> {

        void onGetUserInfoSuccess(UserInfo userProfile);

        void onGetUserInfoFailed(String error);

        void onUnBindWechatSuccess();

        void onUnBindWechatFailed(String error);

        void onBindSocialSuccess(Social social);

        void onBindSocialFailed(String error);

    }

    interface Presenter extends SdBasePresenter {

        void getUserInfo();

        void uploadAvatar(String imageUrl);

        void bindWechat(Activity activity, UMAuthListener umAuthListener);

        void bindSocial(int socialType, String socialInfo);

        void unBindWechat(int socialId);
    }
}
