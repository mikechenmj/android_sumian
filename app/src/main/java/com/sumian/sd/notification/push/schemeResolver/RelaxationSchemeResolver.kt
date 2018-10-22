package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.homepage.RelaxationActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 15:58
 * desc   :
 * version: 1.0
 */
class RelaxationSchemeResolver : SchemeResolver {

    /**
     * sleepdoctor://relaxations?user_id=2102
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        return RelaxationActivity.getLaunchIntent(context)
    }
}