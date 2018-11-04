package com.sumian.sd.network.interceptor

import android.net.Uri
import com.blankj.utilcode.util.LogUtils
import com.sumian.hw.utils.SystemUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.bean.BlueDevice
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
        val appVersion = SystemUtil.getPackageInfo(App.getAppContext()).versionName
        val model = SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()
        val systemVersion = SystemUtil.getSystemVersion()
        val monitorFw = formatMonitorInfo(getFormatMonitorFw())
        val monitorSn = formatMonitorInfo(DeviceManager.getMonitorSn())
        val sleeperFw = formatSpeedSleeperInfo(getFormatSleeperFw())
        val sleeperSn = formatSpeedSleeperInfo(DeviceManager.getSleeperSn())
        val deviceInfo = ("app_version=" + appVersion
                + WITH_SIGN + "model=" + model
                + WITH_SIGN + "system=" + systemVersion
                + WITH_SIGN + "monitor_fw=" + monitorFw
                + WITH_SIGN + "monitor_sn=" + monitorSn
                + WITH_SIGN + "sleeper_fw=" + sleeperFw
                + WITH_SIGN + "sleeper_sn=" + sleeperSn)
        LogUtils.d("device info", deviceInfo)
        return deviceInfo
    }

    private fun getFormatMonitorFw(): String? {
        val monitor = DeviceManager.getMonitorLiveData().value ?: return null
        val channel = when (monitor.channelType) {
            BlueDevice.CHANNEL_TYPE_CLINIC -> "临床"
            BlueDevice.CHANNEL_TYPE_NORMAL -> "正式"
            else -> "null"
        }
        val bomVersion = monitor.sleeperBomVersion
        val bomVersionStr = if (bomVersion == null) "null" else "V$bomVersion"
        return "${monitor.version}-$channel-$bomVersionStr}"
    }

    private fun getFormatSleeperFw(): String? {
        val monitor = DeviceManager.getMonitorLiveData().value ?: return null
        val sleeperBomVersion = monitor.sleeperBomVersion
        val sleeperBomVersionStr = if (sleeperBomVersion == null) "null" else "V$sleeperBomVersion"
        return "${monitor.sleeperVersion}-$sleeperBomVersionStr}"
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
