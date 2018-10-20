package com.sumian.sd.network.interceptor

import android.net.Uri
import com.sumian.hw.common.util.SystemUtil
import com.sumian.hw.device.bean.BlueDevice
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
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

class HwDeviceInfoInterceptor private constructor() : Interceptor {

    companion object {

        private const val WITH_SIGN = "&"

        fun create(): Interceptor {
            return HwDeviceInfoInterceptor()
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
        return ("app_version=" + SystemUtil.getPackageInfo(App.getAppContext()).versionName
                + WITH_SIGN + "model=" + SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()
                + WITH_SIGN + "system=" + SystemUtil.getSystemVersion()
                + WITH_SIGN + "monitor_fw=" + formatMonitorInfo(DeviceManager.getMonitorVersion())
                + WITH_SIGN + "monitor_sn=" + formatMonitorInfo(DeviceManager.getMonitorSn())
                + WITH_SIGN + "sleeper_fw=" + formatSpeedSleeperInfo(DeviceManager.getSleeperVersion())
                + WITH_SIGN + "sleeper_sn=" + formatSpeedSleeperInfo(DeviceManager.getSleeperSn()))
    }

    private fun formatMonitorInfo(monitorInfo: String?): String {
        val bluePeripheral = AppManager.getBlueManager().bluePeripheral
        if (bluePeripheral == null || !bluePeripheral.isConnected) {
            return ""
        }
        return monitorInfo ?: ""
    }

    private fun formatSpeedSleeperInfo(speedSleeperInfo: String?): String {
        val bluePeripheral = AppManager.getBlueManager().bluePeripheral
        if (bluePeripheral == null || !bluePeripheral.isConnected) {
            return ""
        }
        val monitor = DeviceManager.getMonitorLiveData().value
//        return if (monitor?.speedSleeper != null && monitor.speedSleeper?.isConnected != true) {
        return if (monitor == null || monitor.sleeperStatus != BlueDevice.STATUS_CONNECTED) {
            ""
        } else {
            speedSleeperInfo ?: ""
        }
    }

}
