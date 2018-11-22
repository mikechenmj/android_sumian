package com.sumian.common.notification

import android.net.Uri
import android.text.TextUtils
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
 */

class SchemeResolveUtil {
    companion object {

        fun stringToUri(scheme: String): Uri {
            val url = URLDecoder.decode(scheme, "UTF-8")
            return Uri.parse(url)
        }

        fun getParamFromScheme(scheme: String?, key: String): String? {
            if (TextUtils.isEmpty(scheme)) {
                return null
            }
            val uri = stringToUri(scheme!!)
            return uri.getQueryParameter(key)
        }

        fun getNotificationIdFromScheme(scheme: String?): String? {
            return getParamFromScheme(scheme, "notification_id")
        }

        fun getNotificationDataIdFromScheme(scheme: String?): Int? {
            val data_id = getParamFromScheme(scheme, "data_id")
            return if (data_id == null) null else data_id.toInt()
        }
    }
}