package com.sumian.hw.push.schemerecolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.hw.improve.main.HomeActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/27 15:57
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ReportSchemeResolver : ISchemeResolver {
    override fun resolverScheme(context: Context, uri: Uri): Intent? {
        return HomeActivity.getLaunchIntentForPushReport(context, uri.toString())
    }
}