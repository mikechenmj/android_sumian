package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.service.tel.activity.TelBookingDetailActivity

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
class TelBookingDetailSchemeResolver : SchemeResolver {

    /**
     * 医生发送了新的量表
     *  "scheme": "sleepdoctor://booking-detail?id=13646&plan_start_at=0&notification_id=d54211aa-cbd1-476e-838a-a00eec21801a&user_id=2554" //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id").toInt()
        return TelBookingDetailActivity.getLaunchIntent(data)
    }
}