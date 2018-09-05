package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.R
import com.sumian.sd.onlinereport.OnlineReportDetailActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class OnlineReportSchemeResolver : SchemeResolver {

    /**
    电子报告更新
    "scheme" => 'sleepdoctor://online-reports?id=1&url=www.baidu.com&notification_id=9f3f9091-ab98-421c-ac2c-47709c80ba16&user_id=1',   //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val id = uri.getQueryParameter("id")?.toInt() ?: 0
        return OnlineReportDetailActivity.getLaunchIntent(context, id)
    }
}