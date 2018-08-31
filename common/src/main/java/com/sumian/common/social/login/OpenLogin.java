package com.sumian.common.social.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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

    public OpenLogin init(Context context, String appId, String appSecret) {
        umShareAPI = UMShareAPI.get(context);
        PlatformConfig.setWeixin(appId, appSecret);
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
    }

    public void deleteWechatTokenCache(Activity activity, UMAuthListener authListener) {
        umShareAPI.deleteOauth(activity, SHARE_MEDIA.WEIXIN, authListener);
    }
}
