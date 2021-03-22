package com.sumian.sd.common.network.response

import com.google.gson.annotations.SerializedName
import com.sumian.device.data.MonitorChannel
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.buz.upgrade.bean.VersionInfo

/**
 * Created by jzz
 * on 2017/10/31.
 *
 *
 * desc:
 */
data class FirmwareVersionInfo(
        @SerializedName("monitor") private var monitorNormal: VersionInfo? = null,
        var sleeper: VersionInfo? = null,
        @SerializedName("monitor_pro")
        private var monitorPro: VersionInfo? = null
) {
    val monitor get() = if (isMonitorPro()) monitorPro else monitorNormal

    private fun isMonitorPro(): Boolean {
        val channel = DeviceManager.getDevice()?.monitorVersionInfo?.channel
        return channel === MonitorChannel.NORMAL_PRO
    }
}