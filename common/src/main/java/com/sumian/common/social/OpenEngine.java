package com.sumian.common.social;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.DrawableRes;

import com.sumian.common.social.analytics.OpenAnalytics;
import com.sumian.common.social.login.OpenLogin;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;


/**
 * Created by jzz
 * on 2017/12/27.
 * desc:
 */

public class OpenEngine {

    private OpenLogin mOpenLogin;
    private OpenAnalytics mOpenAnalytics;

    /**
     * @param context    上下文，不能为空
     * @param appKey     【友盟+】 AppKey
     * @param channel    【友盟+】 Channel
     * @param pushSecret Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空。
     */
    public static void init(Context context, boolean debug, String appKey, String channel, String pushSecret) {
        UMConfigure.init(context,
                appKey,
                channel,
                UMConfigure.DEVICE_TYPE_PHONE,
                pushSecret);
        UMConfigure.setLogEnabled(debug);
        // 开启日志上报
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    public OpenEngine create(Context context, boolean isDebug, String appId, String appSecret) {
        this.mOpenLogin = new OpenLogin().init(context, appId, appSecret);
        this.mOpenAnalytics = new OpenAnalytics().init(context, isDebug);
        return this;
    }

    public OpenLogin getOpenLogin() {
        return mOpenLogin;
    }

    public OpenAnalytics getOpenAnalytics() {
        return mOpenAnalytics;
    }

    public void shareWeb(Activity activity, String url, String title, String description, String thumb, SHARE_MEDIA shareMedia) {
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(description);
        web.setThumb(new UMImage(activity, thumb));
        new ShareAction(activity)
                .withMedia(web)
                .setPlatform(shareMedia)
                .share();
    }

    public void shareWebForCallback(Activity activity, String url, String title, String description, @DrawableRes int thumb, SHARE_MEDIA shareMedia, UMShareListener umShareListener) {
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(description);
        web.setThumb(new UMImage(activity, thumb));
        new ShareAction(activity)
                .withMedia(web)
                .setCallback(umShareListener)
                .setPlatform(shareMedia)
                .share();
    }
}
