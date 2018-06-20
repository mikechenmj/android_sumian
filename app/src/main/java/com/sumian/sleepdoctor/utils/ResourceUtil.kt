package com.sumian.sleepdoctor.utils

import android.content.Context
import android.support.annotation.StringRes
import com.sumian.sleepdoctor.app.App

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/9 10:33
 *     desc   :
 *     version: 1.0
 * </pre>
 */

fun getAppContext(): Context {
    return App.getAppContext()
}

fun getString(@StringRes stringId: Int): String {
    return getAppContext().resources.getString(stringId)
}