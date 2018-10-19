package com.sumian.sd.notification.push

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.sumian.sd.notification.push.schemeResolver.*
import java.net.URLDecoder

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/9 9:57
 *     desc   :
 *     version: 1.0
 *     测试方式
 *     在LeanCloud(https://leancloud.cn/dashboard/messaging.html?appid=LcAuCbiNkFzWcJiWKdw5gkI5-gzGzoHsz#/message/push/create)
 *     在线发送推送
 *     注意:
 *     推送条件：自定义 -> installationId ,填上logcat 中 打印的installationId，如果发送不成功，卸载重装APP（APP覆盖安装dev，test不同版本的时候，installationId不会刷新）
 *     推送内容：
 *     action要和APP manifest中注册的一致，患者版:"action":"com.sumian.sd.action.PUSH",医生版："action":"com.sumian.sdd.action.PUSH",
 *     scheme中要填写替换的user_id
 *     {
 *          "action":"com.sumian.sd.action.PUSH",
 *          "alert":"CBTI解锁通知",
 *          "scheme":"sleepdoctor://cbti-chapters?notification_id=8e194802-a2bb-47f4-a695-f03ccb5d92ad&user_id=2102&cbti_chapter_id=1"
 *      }
 * </pre>
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
                "diaries" -> DiarySchemeResolver()
                "online-reports" -> OnlineReportSchemeResolver()
                "refund" -> RefundSchemeResolver()
                "advisories" -> AdvisoriesSchemeResolver()
                "scale-distributions" -> ScaleSchemeResolver()
                "referral-notice", "life-notice" -> NotificationSchemeResolver()
                "cbti-chapters" -> CbtiChapterSchemeResolver()
                "cbti-finish-result" -> CbtiChapterSchemeResolver()
                else -> null
            }
        }
    }
}