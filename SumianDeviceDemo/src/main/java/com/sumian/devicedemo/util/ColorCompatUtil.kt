package com.sumian.devicedemo.util

import android.content.Context
import android.os.Build
import androidx.annotation.ColorRes

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/13 22:04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ColorCompatUtil {
    companion object {

        @JvmStatic
        fun getColor(context: Context, @ColorRes colorRes: Int): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(colorRes, context.theme)
            } else {
                @Suppress("DEPRECATION")
                context.resources.getColor(colorRes)
            }
        }
    }
}