package com.sumian.common.h5

import android.content.Context
import android.util.Log
import com.sumian.common.dns.IHttpDns
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener

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

    fun registerX5WebView(context: Context) {
        //内核初始化优化方案,多进程加载
        val map = hashMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        QbSdk.initTbsSettings(map)
        // 非wifi网络条件下是否允许下载内核，默认为false(针对用户没有安装微信/手Q/QQ空间[无内核]的情况下)
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.setNeedInitX5FirstTime(true)
        QbSdk.setTbsListener(object : TbsListener {
            override fun onInstallFinish(p0: Int) {
                if (isDebug()) {
                    Log.e("TAG", "onInstallFinish------>p0=$p0")
                }
            }

            override fun onDownloadFinish(p0: Int) {
                if (isDebug()) {
                    Log.e("TAG", "onDownloadFinish------>p0=$p0")
                }
            }

            override fun onDownloadProgress(p0: Int) {
                if (isDebug()) {
                    Log.e("TAG", "onDownloadProgress------>p0=$p0")
                }
            }
        })
        //x5内核初始化接口
        QbSdk.initX5Environment(context, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                if (isDebug()) {
                    Log.e("TAG", "onCoreInitFinished: --------->")
                }
            }

            override fun onViewInitFinished(isX5Core: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                if (isDebug()) {
                    Log.e("TAG", "onViewInitFinished: ---------->isX5Core=$isX5Core")
                }
            }
        })
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

    fun getHttpDnsEngine(): IHttpDns? {
        return mIHttpDns
    }

    fun setDebug(isDebug: Boolean) {
        mIsDebug = isDebug
    }

    fun isDebug(): Boolean {
        return mIsDebug
    }
}