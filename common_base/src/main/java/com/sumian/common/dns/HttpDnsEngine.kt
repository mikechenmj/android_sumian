package com.sumian.common.dns

import android.content.Context
import android.text.TextUtils
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.HttpDnsService
import java.net.URL

/**
 * Created by dq
 *
 * on 2018/9/25
 *
 * desc:aliyun httpDns 服务
 */
class HttpDnsEngine : IHttpDns {

    private var httpDnsService: HttpDnsService? = null

    override fun init(context: Context, isDebug: Boolean, accountId: String, secret: String): IHttpDns {
        val httpDnsService = HttpDns.getService(context, accountId, secret)
        httpDnsService.setLogEnabled(isDebug)
        httpDnsService.setPreResolveAfterNetworkChanged(true)
        httpDnsService.setExpiredIPEnabled(true)
        httpDnsService.setAuthCurrentTime(System.currentTimeMillis() / 1000L)
        httpDnsService.setHTTPSRequestEnabled(true)
        httpDnsService.setCachedIPEnabled(true)
        this.httpDnsService = httpDnsService
        return this
    }

    override fun setPreHostsList(vararg baseUrl: String) {
        val hosts = arrayListOf<String>()
        baseUrl.forEach {
            hosts.add(URL(it).host)
        }
        httpDnsService?.setPreResolveHosts(hosts)
    }

    override fun getBaseUrlFromHostIp(baseUrl: String): String {
        val url = URL(baseUrl)
        val tmpHost = url.host
        val ipByHostAsync = httpDnsService?.getIpByHostAsync(tmpHost)
        return if (TextUtils.isEmpty(ipByHostAsync)) {
            baseUrl
            //tmpHost
        } else {
            baseUrl.replace(tmpHost, ipByHostAsync!!)
            //ipByHostAsync
        }
    }

    override fun getHostIpFromHostname(hostname: String): String? {
        return httpDnsService?.getIpByHostAsync(hostname)
    }
}