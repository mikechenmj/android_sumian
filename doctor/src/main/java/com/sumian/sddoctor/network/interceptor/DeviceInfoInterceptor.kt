package com.sumian.sddoctor.network.interceptor

import android.net.Uri
import com.blankj.utilcode.util.LogUtils
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.util.SystemUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:设备信息上传拦截器
 */

class DeviceInfoInterceptor private constructor() : Interceptor {

    companion object {
        private const val WITH_SIGN = "&"
        @JvmStatic
        fun create(): Interceptor {
            return DeviceInfoInterceptor()
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
                .request()
                .newBuilder()
                .addHeader("Device-Info", Uri.encode(formatDeviceInfo(), "utf-8"))
                .build()

        return chain.proceed(request)
    }

    private fun formatDeviceInfo(): String {
        //Device-Info": "app_version=速眠-test_1.2.3.1&model=iPhone10,3&system=iOS_11.3.1&monitor_fw=&monitor_sn=&sleeper_fw=&sleeper_sn="
        val appVersion = SystemUtil.getPackageInfo(App.getAppContext())?.versionName ?: ""
        val model = SystemUtil.deviceBrand + " " + SystemUtil.systemModel
        val systemVersion = SystemUtil.systemVersion
        val systemLanguage = SystemUtil.systemLanguage
        val deviceInfo = ("app_version=$appVersion"
                + WITH_SIGN + "model=$model"
                + WITH_SIGN + "system=$systemVersion"
                + WITH_SIGN + "language=$systemLanguage")
        LogUtils.d("device info$", deviceInfo)
        return deviceInfo
    }

}
