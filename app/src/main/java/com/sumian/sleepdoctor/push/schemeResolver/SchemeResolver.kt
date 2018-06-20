package com.sumian.sleepdoctor.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
interface SchemeResolver {
    fun resolverScheme(context : Context,  uri: Uri): Intent
}