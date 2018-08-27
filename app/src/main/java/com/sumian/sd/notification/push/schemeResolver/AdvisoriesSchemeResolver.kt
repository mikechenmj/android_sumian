package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.advisory.activity.AdvisoryDetailActivity

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/11 16:21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class AdvisoriesSchemeResolver : SchemeResolver {
    /**
    图文咨询-医生回复
    "scheme" => 'sleepdoctor://advisories?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")
        return AdvisoryDetailActivity.show(context, data.toInt())
    }
}