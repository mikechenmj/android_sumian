package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.sd.h5.SimpleWebActivity
import java.util.HashMap

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 15:58
 * desc   :
 * version: 1.0
 */
class CbtiFinishReportSchemeResolver : SchemeResolver {

    /**
     * sleepdoctor://cbti-chapters?notification_id=8e194802-a2bb-47f4-a695-f03ccb5d92ad&user_id=2172&cbti_chapter_id=2"  //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("cbti_chapter_id")
        val payloadMap = HashMap<String, Any>(2)
        payloadMap["scale_id"] = "2001,2002,2003"
        payloadMap["chapter_id"] = "1"
        payloadMap["cbti_id"] = "1"
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, "openCbtiScales", payloadMap)
    }
}