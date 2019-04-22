package com.sumian.common.statistic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.tencent.mid.api.MidCallback
import com.tencent.mid.api.MidService
import com.tencent.stat.MtaSDkException
import com.tencent.stat.StatConfig
import com.tencent.stat.StatMultiAccount
import com.tencent.stat.StatService
import com.tencent.stat.common.StatConstants
import java.util.*


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/24 15:53
 * desc   : 埋点统计工具类
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object StatUtil {
    var TAG = javaClass.simpleName
    private lateinit var mContext: Context
    fun init(app: Application, appKey: String, channel: String, debug: Boolean) {
        StatConfig.setDebugEnable(debug)
        StatConfig.setAppKey(app, appKey)
        StatConfig.setInstallChannel(channel)
        StatConfig.setAutoTrackAppsEvent(false)
        mContext = app
        try {
            StatService.startStatService(app, appKey, StatConstants.VERSION)
            StatService.registerActivityLifecycleCallbacks(app)
            Log.d("MTA", "MTA初始化成功")
        } catch (e: MtaSDkException) {
            // MTA初始化失败
            Log.d("MTA", "MTA初始化失败" + e)
        }
        logMid(app)
        StatHybridHandlerForX5.init(app)
    }

    private fun logMid(app: Application) {
        MidService.requestMid(app,
                object : MidCallback {
                    override fun onSuccess(mid: Any) {
                        Log.d("mid", "success to get mid:$mid")
                    }

                    override fun onFail(errCode: Int, msg: String) {
                        Log.d("mid", "failed to get mid, errCode:" + errCode + ",msg:" + msg)
                    }
                })
    }

    fun reportAccount(mobile: String, expireTimeSec: Long) {
        val account = StatMultiAccount(StatMultiAccount.AccountType.PHONE_NO, mobile)
        val time = System.currentTimeMillis() / 1000
        account.lastTimeSec = time
        account.expireTimeSec = expireTimeSec
        StatService.reportMultiAccount(mContext, account)
    }

    fun removeAccount() {
        StatService.removeMultiAccount(mContext, StatMultiAccount.AccountType.PHONE_NO)
    }

    @JvmOverloads
    fun event(eventId: String, properties: Map<String, Any>? = null) {
        val prop = map2prop(properties)
        logEvent(eventId)
        StatService.trackCustomKVEvent(mContext, eventId, prop)
    }

    private fun map2prop(properties: Map<String, Any>?): Properties {
        val prop = Properties()
        if (properties != null) {
            for ((key, value) in properties) {
                prop[key] = value
            }
        }
        return prop
    }

    fun onEventStart(eventId: String, vararg data: String) {
        StatService.trackCustomBeginEvent(mContext, eventId, *data)
    }

    fun onEventStop(eventId: String, vararg data: String) {
        StatService.trackCustomEndEvent(mContext, eventId, *data)
    }

    fun onResume() {
        StatService.onResume(mContext)
    }

    fun onPause() {
        StatService.onPause(mContext)
    }

    fun trackBeginPage(context: Context, pageName: String) {
        logPage(pageName)
        StatService.trackBeginPage(context, pageName)
    }

    fun trackEndPage(context: Context, pageName: String) {
        StatService.trackEndPage(context, pageName)
    }

    private fun logEvent(msg: String) {
        Log.d(TAG, "event: $msg")
    }

    private fun logPage(msg: String) {
        Log.d(TAG, "page: $msg")
    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    fun trackCustomBeginKVEvent(eventName: String, prop: Map<String, String>? = null) {
        StatService.trackCustomBeginKVEvent(mContext, eventName, map2prop(prop))
    }

    fun trackCustomEndKVEvent(eventName: String, prop: Map<String, String>? = null) {
        StatService.trackCustomEndKVEvent(mContext, eventName, map2prop(prop))
    }
}