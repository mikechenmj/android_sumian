package com.sumian.sleepdoctor.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sleepdoctor.notification.RefundActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class RefundSchemeResolver : SchemeResolver {

    /**
    退款成功通知
    "scheme" => 'sleepdoctor://refund?order_no=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("order_no")
        return RefundActivity.getLaunchIntent(context, data)
    }
}