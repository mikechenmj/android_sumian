@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.util

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.sumian.sddoctor.app.App

/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc:
 */
object ResUtils {

    @JvmStatic
    fun getColor(@ColorRes colorIdRes: Int): Int {
        return App.getAppContext().resources.getColor(colorIdRes)
    }

    fun getString(@StringRes strIdRes: Int): String {
        return App.getAppContext().getString(strIdRes)
    }

}