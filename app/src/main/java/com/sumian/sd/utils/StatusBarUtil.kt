package com.sumian.sd.utils

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
        fun setStatusBarColor(activity: Activity, color: Int) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }

        /**
         * isStatusBarBgDark true：文字白色，false文字黑色
         */
        fun setStatusBarTextColor(activity: Activity, isStatusBarBgDark: Boolean) {
            // Fetch the current flags.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val flags = activity.window.decorView.systemUiVisibility
                // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
                activity.window.decorView.systemUiVisibility =
                        if (isStatusBarBgDark) (flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()) else (flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        fun setStatusBarColor(activity: Activity, color: Int, isStatusBarDark: Boolean) {
            setStatusBarColor(activity, color)
            setStatusBarTextColor(activity, isStatusBarDark)
        }
    }
}