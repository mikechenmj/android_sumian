package com.sumian.common.log

import android.annotation.SuppressLint
import android.content.Context
import com.aliyun.sls.android.sdk.ClientConfiguration
import com.aliyun.sls.android.sdk.SLSDatabaseManager
import com.aliyun.sls.android.sdk.SLSLog
import com.aliyun.sls.android.sdk.LOGClient
import com.aliyun.sls.android.sdk.LogException
import com.aliyun.sls.android.sdk.core.auth.PlainTextAKSKCredentialProvider
import com.aliyun.sls.android.sdk.core.auth.StsTokenCredentialProvider
import com.aliyun.sls.android.sdk.request.PostLogRequest
import com.aliyun.sls.android.sdk.result.PostLogResult
import com.aliyun.sls.android.sdk.core.callback.CompletedCallback
import com.aliyun.sls.android.sdk.model.Log
import com.aliyun.sls.android.sdk.model.LogGroup
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.AVOSCloud.applicationContext
import com.blankj.utilcode.util.LogUtils
import java.lang.reflect.Executable
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 13:40
 * desc   : ref
 * https://github.com/aliyun/aliyun-log-android-sdk/blob/master/README-CN.md
 * https://help.aliyun.com/document_detail/62681.html
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object AliyunLogManager {
    private lateinit var mLogClient: LOGClient
    private var mProjectName: String = ""
    private var mLogStore = ""

    fun init(applicationContext: Context, accessKey: String, accessSecret: String,
             projectName: String, logStore: String, endPoint: String) {
        mProjectName = projectName
        mLogStore = logStore

        SLSDatabaseManager.getInstance().setupDB(applicationContext);
        SLSLog.enableLog() // log打印在控制台

        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        conf.cachable = true     // 设置日志发送失败时，是否支持本地缓存。
        conf.connectType = ClientConfiguration.NetworkPolicy.WWAN_OR_WIFI   // 设置缓存日志发送的网络策略
        mLogClient = LOGClient(AVOSCloud.applicationContext, endPoint, PlainTextAKSKCredentialProvider(accessKey, accessSecret), conf)
    }

    fun log(map: Map<String, String>) {
        val logGroup = LogGroup("topic_default", "source_default")
        val log = Log()
        for ((k, v) in map) {
            log.PutContent(k, v)
        }
        log.PutTime((System.currentTimeMillis() / 1000).toInt())
        logGroup.PutLog(log)
        try {
            val request = PostLogRequest(mProjectName, mLogStore, logGroup)
            mLogClient.asyncPostLog(request, object : CompletedCallback<PostLogRequest, PostLogResult> {
                override fun onSuccess(request: PostLogRequest, result: PostLogResult) {
                    LogUtils.d(result)
                }

                override fun onFailure(request: PostLogRequest, exception: LogException) {
                    LogUtils.d(exception)
                }
            })
        } catch (e: LogException) {
            e.printStackTrace()
        }

    }
}