package com.sumian.sleepdoctor.account.userProfile.contract;

import android.app.Activity;

import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
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
public interface UserInfoContract {

    interface View extends BaseView<Presenter> {

        void onGetUserInfoSuccess(UserProfile userProfile);

        void onGetUserInfoFailed(String error);

        void onUnBindWechatSuccess();

        void onUnBindWechatFailed(String error);

        void onBindSocialSuccess(Social social);

        void onBindSocialFailed(String error);

    }

    interface Presenter extends BasePresenter {

        void getUserInfo();

        void uploadAvatar(String imageUrl);

        void bindWechat(Activity activity, UMAuthListener umAuthListener);

        void bindSocial(int socialType, String socialInfo);

        void unBindWechat(int socialId);
    }
}
