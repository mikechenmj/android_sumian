package com.sumian.app.push

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.app.push.schemerecolver.ISchemeResolver
import com.sumian.app.push.schemerecolver.ReportSchemeResolver
import java.net.URLDecoder

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/27 11:19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SchemeUtil {
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
            return createSchemeResolver(uri)?.resolverScheme(context, uri) ?: return null
        }

        //        {"alert":"【医生评价】医生对您的睡眠日记报进行了评价，点击通知查看评价详情。","action":"com.tech.sumian.action.PUSH","scheme":"http://www.sumian.com/day_report?date=1526313600&user_id=2102","extraInfo":[]}
        // {"alert":"【医生评价】医生对您的睡眠周报告进行了评价，点击通知查看评价详情。","action":"com.tech.sumian.action.PUSH","scheme":"http://www.sumian.com/week_report?date=1526126401&user_id=2102","extraInfo":[]}
        fun createSchemeResolver(uri: Uri): ISchemeResolver? {
            val path = uri.path
            LogUtils.d(uri.host, path,uri.pathSegments,uri.scheme)
            when (path) {
                "/day_report","/week_report" -> {
                    return ReportSchemeResolver()
                }
            }
            return null
        }

        fun resolveScheme(context: Context, scheme: String?): Intent? {
            if (TextUtils.isEmpty(scheme)) {
                return null
            }
            val uri = Uri.parse(scheme)
            return createSchemeResolver(uri)?.resolverScheme(context, uri)
        }
    }
}