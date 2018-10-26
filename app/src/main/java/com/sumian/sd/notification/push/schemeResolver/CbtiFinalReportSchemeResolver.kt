package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.h5.SimpleWebActivity
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 15:58
 * desc   :
 * version: 1.0
 */
class CbtiFinalReportSchemeResolver : SchemeResolver {

    /**
     * "scheme": "sleepdoctor://cbti-final-reports?scale_distribution_ids=1,2,3&cbti_id=1&chapter_id=1&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2102"
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val payloadMap = HashMap<String, Any?>(3)
        payloadMap["scale_id"] = uri.getQueryParameter("scale_distribution_ids")
        payloadMap["cbti_id"] = uri.getQueryParameter("cbti_id")
        payloadMap["chapter_id"] = uri.getQueryParameter("chapter_id")
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, "openCbtiScales", payloadMap)
    }
}