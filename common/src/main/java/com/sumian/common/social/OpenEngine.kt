package com.sumian.common.social

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import com.sumian.common.h5.bean.ShareData
import com.sumian.common.social.analytics.OpenAnalytics
import com.sumian.common.social.login.OpenLogin
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import java.io.File


/**
 * Created by jzz
 * on 2017/12/27.
 * desc:
 */

class OpenEngine {

    var openLogin: OpenLogin? = null
        private set
    var openAnalytics: OpenAnalytics? = null
        private set

    fun create(context: Context, isDebug: Boolean, appId: String, appSecret: String): OpenEngine {
        this.openLogin = OpenLogin().init(context, appId, appSecret)
        this.openAnalytics = OpenAnalytics().init(context, isDebug)
        return this
    }

    fun shareUrl(activity: Activity, url: String, title: String, description: String, @DrawableRes thumb: Int, shareMedia: SHARE_MEDIA, umShareListener: UMShareListener?) {
        val web = UMWeb(url)
        web.title = title
        web.description = description
        web.setThumb(UMImage(activity, thumb))
        ShareAction(activity)
                .withMedia(web)
                .setCallback(umShareListener)
                .setPlatform(shareMedia)
                .share()
    }

    fun shareUrl(activity: Activity, url: String?, title: String?, description: String?, thumbUrl: String?, shareMedia: SHARE_MEDIA, umShareListener: UMShareListener? = null) {
        val web = UMWeb(url)
        web.title = title
        web.description = description
        web.setThumb(UMImage(activity, thumbUrl))
        ShareAction(activity)
                .withMedia(web)
                .setCallback(umShareListener)
                .setPlatform(shareMedia)
                .share()
    }

    fun shareImageFile(activity: Activity, file: File, text: String, shareMedia: SHARE_MEDIA, umShareListener: UMShareListener? = null) {
        val image = UMImage(activity, file)//本地文件
        image.setThumb(UMImage(activity, file))
        ShareAction(activity)
                .setPlatform(shareMedia)
                .withText(text)
                .withMedia(image)
                .setCallback(umShareListener)
                .share();
    }

    fun shareUrl(activity: Activity, shareData: ShareData, umShareListener: UMShareListener? = null) {
        shareUrl(activity, shareData.url, shareData.title, shareData.desc, shareData.icon, shareData.getPlatformEnum(), umShareListener)
    }

    companion object {

        /**
         * @param context    上下文，不能为空
         * @param appKey     【友盟+】 AppKey
         * @param channel    【友盟+】 Channel
         * @param pushSecret Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空。
         */
        fun init(context: Context, debug: Boolean, appKey: String, channel: String, pushSecret: String) {
            UMConfigure.init(context,
                    appKey,
                    channel,
                    UMConfigure.DEVICE_TYPE_PHONE,
                    pushSecret)
            UMConfigure.setLogEnabled(debug)
            // 开启日志上报
            MobclickAgent.setCatchUncaughtExceptions(true)
        }
    }
}
