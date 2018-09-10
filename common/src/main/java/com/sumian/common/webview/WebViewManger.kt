package com.sumian.common.webview

import android.text.TextUtils

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

    companion object {
        private val INSTANCE by lazy {
            WebViewManger()
        }

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
}