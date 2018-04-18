package com.sumian.open.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sumian.open.BuildConfig;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by jzz
 * on 2017/12/14.
 * desc:
 */

public class OpenLogin {

    private UMShareAPI umShareAPI;

    public OpenLogin init(Context context, boolean isDebug) {
        Config.DEBUG = isDebug;
        umShareAPI = UMShareAPI.get(context);
        PlatformConfig.setWeixin(BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
        return this;
    }


    public void weChatLogin(Activity activity, UMAuthListener authListener) {
        login(activity, SHARE_MEDIA.WEIXIN, authListener);
    }


    public void delegateActivityResult(int requestCode, int resultCode, Intent data) {
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    private void login(Activity activity, SHARE_MEDIA shareMedia, UMAuthListener authListener) {
        umShareAPI.getPlatformInfo(activity, shareMedia, authListener);
        //umShareAPI.doOauthVerify(activity, shareMedia, authListener);
    }

}
