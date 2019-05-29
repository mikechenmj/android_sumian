package com.sumian.common.h5.bean

import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/23 14:54
 * desc   :
 * version: 1.0
 */
data class ShareData(
        val platform: String?,
        val title: String?,
        val desc: String?,
        val icon: String?,
        val url: String?
) {
    fun getPlatformEnum(): SHARE_MEDIA {
        return when (platform) {
            "WEIXIN" -> SHARE_MEDIA.WEIXIN
            "WEIXIN_CIRCLE" -> SHARE_MEDIA.WEIXIN_CIRCLE
            "SINA" -> SHARE_MEDIA.SINA
            else -> SHARE_MEDIA.WEIXIN_CIRCLE
        }
    }
}