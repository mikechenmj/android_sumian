package com.sumian.common.dns

import android.content.Context

/**
 * Created by dq
 *
 * on 2018/9/25
 *
 * desc:
 */
interface IHttpDns {

    fun init(context: Context, isDebug: Boolean, accountId: String, secret: String): IHttpDns

    fun setPreHostsList(vararg baseUrl: String)

    fun getBaseUrlFromHostIp(baseUrl: String): String

    fun  getHostIpFromHostname(hostname:String):String?


}