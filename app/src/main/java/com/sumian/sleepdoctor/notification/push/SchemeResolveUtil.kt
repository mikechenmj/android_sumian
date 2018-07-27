package com.sumian.sleepdoctor.notification.push

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.sumian.sleepdoctor.notification.push.schemeResolver.*
import java.net.URLDecoder

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/9 9:57
 *     desc   :
 *     version: 1.0
 * </pre>
 */

/**
 *
医生建议更新
"scheme" => 'sleepdoctor://diaries?date=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
电子报告更新
"scheme" => 'sleepdoctor://online-reports?id=1&url=www.baidu.com&notification_id=9f3f9091-ab98-421c-ac2c-47709c80ba16&user_id=1',   //urlencode后
退款成功通知
"scheme" => 'sleepdoctor://refund?order_no=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
图文咨询-医生回复
"scheme" => 'sleepdoctor://advisories?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
医生发送了新的量表
"scheme" => 'sleepdoctor://scale-distributions?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
医生随访提醒 - 复诊提醒
"scheme" => 'sleepdoctor://referral-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
医生随访提醒 - 生活提醒
"scheme" => 'sleepdoctor://life-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
 */
class SchemeResolveUtil {
    companion object {
        fun getUserIdFromScheme(scheme: String?): String? {
            if (TextUtils.isEmpty(scheme)) {
                return null
            }
            val decodedScheme = URLDecoder.decode(scheme, "UTF-8")
            val uri = Uri.parse(decodedScheme)
            return uri.getQueryParameter("user_id")
        }

        fun schemeResolver(context: Context, scheme: String): Intent? {
            val url = URLDecoder.decode(scheme, "UTF-8")
            val uri = Uri.parse(url)
            return createSchemeResolver(uri)?.resolveScheme(context, uri) ?: return null
        }

        private fun createSchemeResolver(uri: Uri): SchemeResolver? {
            return when (uri.host) {
                "diaries" -> {
                    DiarySchemeResolver()
                }
                "online-reports" -> {
                    OnlineReportSchemeResolver()
                }
                "refund" -> {
                    RefundSchemeResolver()
                }
                "advisories" -> {
                    AdvisoriesSchemeResolver()
                }
                "scale-distributions" -> {
                    ScaleSchemeResolver()
                }
                "referral-notice", "life-notice" -> {
                    NotificationSchemeResolver()
                }
                else -> null
            }
        }
    }
}