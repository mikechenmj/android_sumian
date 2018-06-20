package com.sumian.sleepdoctor.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sleepdoctor.notification.NotificationListActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class NotificationSchemeResolver : SchemeResolver {

    /**
    医生随访提醒 - 复诊提醒
    "scheme" => 'sleepdoctor://referral-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
    医生随访提醒 - 生活提醒
    "scheme" => 'sleepdoctor://life-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
     */
    override fun resolverScheme(context: Context, uri: Uri): Intent {
        return NotificationListActivity.getLaunchIntent(context)
    }
}