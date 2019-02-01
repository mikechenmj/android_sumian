package com.sumian.sddoctor.network

import com.sumian.common.network.NetEngine
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.log.SddLogInterceptor
import com.sumian.sddoctor.network.dns.SdHttpDns
import com.sumian.sddoctor.network.interceptor.DeviceInfoInterceptor
import com.sumian.sddoctor.network.interceptor.NormalInterceptor
import com.sumian.sddoctor.network.interceptor.TokenAuthInterceptor

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:  sumian 网络引起 注册管理类
 */

class NetworkManager private constructor() {

    private val mSddApi: NetApi  by lazy {
        NetEngine.NetEngineBuilder()
                .isDebug(BuildConfig.DEBUG)
                .baseUrl(BuildConfig.BASE_URL)
                .dns(SdHttpDns.create())
                .addInterceptor(
                        TokenAuthInterceptor.create(),
                        NormalInterceptor.create(),
                        SddLogInterceptor(),
                        DeviceInfoInterceptor.create())
                .build()
                .create(NetApi::class.java)
    }

    companion object {

        @JvmStatic
        fun create(): NetworkManager {
            return NetworkManager()
        }

    }

    fun installSddHttpRequest(): NetApi {
        return mSddApi
    }


}
