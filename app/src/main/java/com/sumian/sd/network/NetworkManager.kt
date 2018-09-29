package com.sumian.sd.network

import com.sumian.common.network.NetEngine
import com.sumian.sd.BuildConfig
import com.sumian.sd.network.api.HwApi
import com.sumian.sd.network.api.SdApi
import com.sumian.sd.network.dns.SdHttpDns
import com.sumian.sd.network.interceptor.HwDeviceInfoInterceptor
import com.sumian.sd.network.interceptor.NormalInterceptor
import com.sumian.sd.network.interceptor.TokenAuthInterceptor

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
                .addInterceptor(TokenAuthInterceptor.create()
                        , NormalInterceptor.create())
                .build()
                .create(SdApi::class.java)
    }

    private val mHwApi: HwApi  by lazy {
        NetEngine.NetEngineBuilder()
                .isDebug(BuildConfig.DEBUG)
                .baseUrl(BuildConfig.HW_BASE_URL)
                .dns(SdHttpDns.create())
                .addInterceptor(TokenAuthInterceptor.create()
                        , NormalInterceptor.create()
                        , HwDeviceInfoInterceptor.create())
                .build()
                .create(HwApi::class.java)
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

    fun installHwHttpRequest(): HwApi {
        return mHwApi
    }


}
