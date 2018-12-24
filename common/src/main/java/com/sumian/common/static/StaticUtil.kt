package com.sumian.common.static

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.tencent.stat.common.StatConstants
import com.tencent.mid.api.MidCallback
import com.tencent.mid.api.MidService
import com.tencent.stat.*
import java.util.*


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/24 15:53
 * desc   :
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object StaticUtil {
    private lateinit var mContext: Context
    fun init(app: Application, appKey: String, channel: String, debug: Boolean) {
        StatConfig.setDebugEnable(debug)
        StatConfig.setAppKey(app, appKey)
        StatConfig.setInstallChannel(channel)
        mContext = app
        try {
            StatService.startStatService(app, appKey, StatConstants.VERSION)
            StatService.registerActivityLifecycleCallbacks(app)
            Log.d("MTA", "MTA初始化成功")
        } catch (e: MtaSDkException) {
            // MTA初始化失败
            Log.d("MTA", "MTA初始化失败" + e)
        }

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

    fun event(eventId: String, properties: Map<String, String>? = null) {
        val prop = Properties()
        if (properties != null) {
            for ((key, value) in properties) {
                prop[key] = value
            }
        }
        StatService.trackCustomKVEvent(mContext, eventId, prop)
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
}