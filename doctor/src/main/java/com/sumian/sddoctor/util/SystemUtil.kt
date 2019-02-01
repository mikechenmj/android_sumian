package com.sumian.sddoctor.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.util.*

/**
 * Created by sm
 * on 2018/5/18 13:46
 * desc:
 */
class SystemUtil {

    companion object {

        /**
         * 获取当前手机系统版本号
         *
         * @return 系统版本号
         */
        val systemVersion: String
            get() = "Android " + android.os.Build.VERSION.RELEASE

        /**
         * 获取手机型号
         *
         * @return 手机型号
         */
        val systemModel: String
            get() = android.os.Build.MODEL

        /**
         * 获取手机厂商
         *
         * @return 手机厂商
         */
        val deviceBrand: String
            get() = android.os.Build.BRAND

        /**
         * 获取当前手机系统语言。
         *
         * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
         */
        val systemLanguage: String
            get() = Locale.getDefault().language

        /**
         * 2  * 获取版本号
         * 3  * @return 当前应用的版本号
         * 4
         */
        @JvmStatic
        fun getPackageInfo(context: Context): PackageInfo? {
            val manager = context.packageManager
            var info: PackageInfo? = null
            try {
                info = manager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return info
        }


    }


}
