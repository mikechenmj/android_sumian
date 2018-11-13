package com.sumian.common.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/13 14:41
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class StatusBarUtil {
    companion object {
        @JvmStatic
        fun setStatusBarColor(activity: Activity, color: Int) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }

        /**
         * isDark true：文字黑色，false文字白色
         */
        @JvmStatic
        fun setStatusBarTextColorDark(activity: Activity, isDark: Boolean) {
            // Fetch the current flags.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val flags = activity.window.decorView.systemUiVisibility
                // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
                activity.window.decorView.systemUiVisibility =
                        if (!isDark) (flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()) else (flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        @JvmStatic
        fun setStatusBarColor(activity: Activity, color: Int, isStatusBarDark: Boolean) {
            setStatusBarColor(activity, color)
            setStatusBarTextColorDark(activity, isStatusBarDark)
        }
    }
}