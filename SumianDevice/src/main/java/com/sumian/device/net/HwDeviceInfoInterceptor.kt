package com.sumian.device.net

import android.net.Uri
import com.sumian.device.util.DeviceInfoFormatter
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

class HwDeviceInfoInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
                .request()
                .newBuilder()
                .addHeader("Device-Info", Uri.encode(DeviceInfoFormatter.getFormatedDeviceInfo(), "utf-8"))
                .build()

        return chain.proceed(request)
    }
}
