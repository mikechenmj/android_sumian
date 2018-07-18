package com.sumian.sleepdoctor.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sleepdoctor.record.SleepRecordDetailActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DiarySchemeResolver : SchemeResolver {

    /**
    医生建议更新
    "scheme" => 'sleepdoctor://diaries?date=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val date = uri.getQueryParameter("date")
        val dateInMills = date.toInt() * 1000L
        return SleepRecordDetailActivity.getLaunchIntent(context, dateInMills)
    }
}