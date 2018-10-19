package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.service.cbti.activity.CBTIWeekCoursePartActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 15:58
 * desc   :
 * version: 1.0
 */
class CbtiChapterSchemeResolver : SchemeResolver {

    /**
     * sleepdoctor://cbti-chapters?notification_id=8e194802-a2bb-47f4-a695-f03ccb5d92ad&user_id=2172&cbti_chapter_id=2"  //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("cbti_chapter_id")
        return CBTIWeekCoursePartActivity.getLaunchIntent(context, data.toInt())
    }
}