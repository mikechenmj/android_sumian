package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.service.tel.activity.TelBookingListActivity

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
class BookingListSchemeResolver : SchemeResolver {

    /**
    "scheme" => "sleepdoctor://booking-list?list_type=0&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2040" //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("list_type").toInt()
        return TelBookingListActivity.getLaunchIntent(data)
    }
}