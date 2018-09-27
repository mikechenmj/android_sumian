package com.sumian.common.h5

import com.sumian.common.dns.IHttpDns

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/10 11:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class WebViewManger private constructor() {

    private var mBaseUrl: String? = null
    private var mToken: String? = null
    private var mIHttpDns: IHttpDns? = null
    private var mIsDebug: Boolean = false

    companion object {
        private val INSTANCE by lazy {
            WebViewManger()
        }

        @JvmStatic
        fun getInstance(): WebViewManger {
            return INSTANCE
        }
    }

    fun setBaseUrl(url: String) {
        mBaseUrl = url
    }

    fun getBaseUrl(): String? {
        return mBaseUrl
    }

    fun setToken(token: String?) {
        mToken = token
    }

    fun getToken(): String? {
        return mToken
    }

    fun registerHttpDnsEngine(iHttpDns: IHttpDns) {
        mIHttpDns = iHttpDns
    }

    fun getHttpDnsEngine(): IHttpDns {
        return mIHttpDns!!
    }

    fun setDebug(isDebug: Boolean) {
        mIsDebug = isDebug
    }

    fun isDebug(): Boolean {
        return mIsDebug
    }
}