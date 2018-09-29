package com.sumian.sd.network.dns

import android.text.TextUtils
import com.sumian.sd.app.AppManager
import okhttp3.Dns
import java.net.InetAddress

/**
 * Created by  sm
 *
 * on 2018/9/29
 *
 *desc: sumian  Http dns   使用 aliyun httpDns 直接解析 dns 服务器
 *
 */
class SdHttpDns private constructor() : Dns {

    companion object {

        @JvmStatic
        fun create(): Dns {
            return SdHttpDns()
        }
    }

    override fun lookup(hostname: String): MutableList<InetAddress> {
        val hostIpFrom = AppManager.getHttpDns().getHostIpFromHostname(hostname)
        return if (TextUtils.isEmpty(hostIpFrom)) {
            Dns.SYSTEM.lookup(hostname)
        } else {
            InetAddress.getAllByName(hostIpFrom).toMutableList()
        }
    }
}