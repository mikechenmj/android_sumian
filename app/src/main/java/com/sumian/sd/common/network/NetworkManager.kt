package com.sumian.sd.common.network

import com.sumian.common.network.NetEngine
import com.sumian.sd.BuildConfig
import com.sumian.sd.common.network.api.SdApi
import com.sumian.sd.common.network.dns.SdHttpDns
import com.sumian.sd.common.network.interceptor.HwDeviceInfoInterceptor
import com.sumian.sd.common.network.interceptor.NormalInterceptor
import com.sumian.sd.common.network.interceptor.SdLogInterceptor
import com.sumian.sd.common.network.interceptor.TokenAuthInterceptor

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:  sumian 网络引起 注册管理类
 */

class NetworkManager private constructor() {

    private val mSdApi: SdApi  by lazy {
        NetEngine.NetEngineBuilder()
                .isDebug(BuildConfig.DEBUG)
                .baseUrl(BuildConfig.BASE_URL)
                .dns(SdHttpDns.create())
                .addInterceptor(TokenAuthInterceptor.create(),
                        NormalInterceptor.create(),
                        HwDeviceInfoInterceptor(),
                        SdLogInterceptor()
                )
                .build()
                .create(SdApi::class.java)
    }

    companion object {

        @JvmStatic
        fun create(): NetworkManager {
            return NetworkManager()
        }

    }

    fun installSdHttpRequest(): SdApi {
        return mSdApi
    }
}
