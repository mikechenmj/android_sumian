package com.sumian.sd.buz.account.userProfile;

import com.sumian.sd.base.SdBaseView;
import com.sumian.sd.buz.account.bean.Social;
import com.sumian.sd.buz.account.bean.UserInfo;

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

    interface View extends SdBaseView<SdUserInfoPresenter> {

        void onGetUserInfoSuccess(UserInfo userProfile);

        void onGetUserInfoFailed(String error);

        void onUnBindWechatSuccess();

        void onUnBindWechatFailed(String error);

        void onBindSocialSuccess(Social social);

        void onBindSocialFailed(String error);

    }
}
