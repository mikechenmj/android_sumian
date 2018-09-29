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
        NetEngine.NetEngineBuilder<SdApi>()
                .isDebug(BuildConfig.DEBUG)
                .addBaseNetApi(SdApi::class.java)
                .addBaseUrl(BuildConfig.BASE_URL)
                .addDns(SdHttpDns.create())
                .addInterceptor(TokenAuthInterceptor.create()
                        , NormalInterceptor.create())
                .build().getNetEngineApi()
    }

    private val mHwApi: HwApi  by lazy {
        NetEngine.NetEngineBuilder<HwApi>()
                .isDebug(BuildConfig.DEBUG)
                .addBaseNetApi(HwApi::class.java)
                .addBaseUrl(BuildConfig.HW_BASE_URL)
                .addDns(SdHttpDns.create())
                .addInterceptor(TokenAuthInterceptor.create()
                        , NormalInterceptor.create()
                        , HwDeviceInfoInterceptor.create())
                .build().getNetEngineApi()
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
