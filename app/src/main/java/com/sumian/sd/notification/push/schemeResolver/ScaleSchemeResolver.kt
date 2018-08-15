package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.R
import com.sumian.sd.scale.ScaleDetailActivity

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ScaleSchemeResolver : SchemeResolver {

    /**
    医生发送了新的量表
    "scheme" => 'sleepdoctor://scale-distributions?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")
        val title = context.getString(R.string.record_weekly_report) // td 让服务器在 scheme 加上 title 字段
        return ScaleDetailActivity.getLaunchIntent(context, title, data.toLong())
    }
}