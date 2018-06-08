package com.sumian.sleepdoctor.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.sumian.sleepdoctor.main.MainActivity

/**
 * <pre>
 *     author : Zhan Xuzhao
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
    override fun resolverScheme(context: Context, uri: Uri): Intent {
        val intent: Intent?
        val bundle = Bundle()
        val date = uri.getQueryParameter("date")
        val dateInMills = date.toInt() * 1000L
        bundle.putInt(MainActivity.KEY_TAB_INDEX, 0)
        bundle.putLong(MainActivity.KEY_SLEEP_RECORD_TIME, dateInMills)
        bundle.putBoolean(MainActivity.KEY_SCROLL_TO_BOTTOM, true)
        intent = Intent(context, MainActivity::class.java)
        intent.putExtras(bundle)
        return intent
    }
}