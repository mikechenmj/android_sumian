package com.sumian.common.notification

import android.content.Intent
import android.os.Bundle
import com.sumian.common.utils.JsonUtil

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 11:17
 *     desc   : 解析 LeanCloud 发送过来的 broadcast 中的数据
 *     reference: https://leancloud.cn/docs/android_push_guide.html#hash1393576931
 *     version: 1.0
 * </pre>
 */
object PushDataResolveUtil {
    private const val KEY_PREFIX = "com.avos.avoscloud"
    private const val KEY_CHANNEL = "$KEY_PREFIX.Channel"
    private const val KEY_DATA = "$KEY_PREFIX.Data"

    fun getPushAction(intent: Intent?): String? {
        return intent?.action
    }

    fun getPushChannel(intent: Intent?): String? {
        return intent?.extras!!.getString(KEY_CHANNEL)
    }

    fun getPushData(intent: Intent?): PushData? {
        val pushDataJson = intent?.extras?.getString(KEY_DATA) ?: return null
        return JsonUtil.fromJson(pushDataJson, PushData::class.java)
    }

    fun getOriginPushData(bundle: Bundle?): String? {
        return bundle?.getString("com.avoscloud.Data")
    }
}