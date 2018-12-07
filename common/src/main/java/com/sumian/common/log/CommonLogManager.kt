package com.sumian.common.log

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NetworkUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 14:54
 * desc   : ref
 * https://www.tapd.cn/21254041/prong/stories/view/1121254041001003070?url_cache_key=aec350d6d1ce106539a689985e57282f&action_entry_type=stories
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")
abstract class CommonLogManager {
    companion object {
        const val KEY_CLIENT_TYPE = "client_type"
        const val KEY_NETWORK = "network"
        const val KEY_TIME_IN_MILLIS = "time_in_millis"
        const val KEY_FORMAT_TIME = "format_time"
        const val KEY_ACTION_TYPE = "action_type"
        const val KEY_MOBILE = "mobile"
        const val KEY_USER_ID = "user_id"
        const val KEY_PAGE_DATA = "page_data"
        const val KEY_HTTP_REQUEST = "http_request"
        const val KEY_HTTP_RESPONSE = "http_response"
        const val KEY_APP_VERSION = "app_version"
        const val KEY_APP_TYPE = "app_type"
        const val KEY_DEVICE_INFO = "device_info"
        const val KEY_REMARK = "remark"
        const val KEY_USER_AGENT = "user_agent"

        const val CLIENT_TYPE_H5 = "H5"
        const val CLIENT_TYPE_ANDROID = "Android"
        const val CLIENT_TYPE_IOS = "iOS"

        const val APP_TYPE_SD = "sd"
        const val APP_TYPE_SDD = "sdd"

        const val NETWORK_TYPE_WIFI = "wifi"
        const val NETWORK_TYPE_4G = "4G"
        const val NETWORK_TYPE_3G = "3G"
        const val NETWORK_TYPE_2G = "2G"
        const val NETWORK_TYPE_UNKNOWN = "unknown"

        const val ACTION_TYPE_PAGE = "page"
        const val ACTION_TYPE_HTTP = "http"
        const val ACTION_TYPE_DEVICE = "device"

        const val PAGE_OPERATION = ""
    }

    private var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private var date = Date()
    private var clientType = CLIENT_TYPE_ANDROID
    private var network = NETWORK_TYPE_UNKNOWN
    private var appVersion = ""

    fun init(context: Context, accessKey: String, accessSecret: String,
             projectName: String, logStore: String, endPoint: String) {
        observeUserInfo()
        observeNetworkState(context)
        AliyunLogManager.init(context,
                accessKey,
                accessSecret,
                projectName,
                logStore,
                endPoint
        )
        network = getNetworkTypeString()
        appVersion = AppUtils.getAppVersionName()
    }

    fun createTemplateLogMap(): MutableMap<String, String> {
        return mapOf(
                KEY_CLIENT_TYPE to clientType,
                KEY_NETWORK to network,
                KEY_FORMAT_TIME to getTime(),
                KEY_TIME_IN_MILLIS to System.currentTimeMillis().toString(),
                KEY_MOBILE to getMobile(),
                KEY_USER_ID to getUserId(),
                KEY_APP_TYPE to getAppType(),
                KEY_APP_VERSION to appVersion,
                KEY_DEVICE_INFO to getDeviceInfo()
        )
                .toMutableMap()
    }

    private fun getTime(): String {
        date.time = System.currentTimeMillis()
        return timeFormat.format(date)
    }

    fun log(map: Map<String, String>) {
        AliyunLogManager.log(addMap(createTemplateLogMap(), map))
    }

    fun logPage(pageClassName: String, open: Boolean) {
        log(mapOf(
                KEY_ACTION_TYPE to ACTION_TYPE_PAGE,
                KEY_PAGE_DATA to "$pageClassName ${if (open) "打开" else "关闭"}"
        ))
    }

    fun logDevice(s: String) {
        log(mapOf(
                KEY_ACTION_TYPE to ACTION_TYPE_DEVICE,
                KEY_REMARK to s
        ))
    }

    fun logHttp(request: String?, response: String?) {
        log(mapOf(
                KEY_ACTION_TYPE to ACTION_TYPE_HTTP,
                KEY_HTTP_REQUEST to (request ?: ""),
                KEY_HTTP_RESPONSE to (response ?: "")
        ))
    }

    private fun observeNetworkState(context: Context) {
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                network = getNetworkTypeString()
            }
        }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun getNetworkTypeString(): String {
        return when (NetworkUtils.getNetworkType()) {
            NetworkUtils.NetworkType.NETWORK_2G -> NETWORK_TYPE_2G
            NetworkUtils.NetworkType.NETWORK_3G -> NETWORK_TYPE_3G
            NetworkUtils.NetworkType.NETWORK_4G -> NETWORK_TYPE_4G
            NetworkUtils.NetworkType.NETWORK_WIFI -> NETWORK_TYPE_WIFI
            else -> NETWORK_TYPE_UNKNOWN
        }
    }

    abstract fun observeUserInfo()

    abstract fun getMobile(): String

    abstract fun getAppType(): String

    abstract fun getUserId(): String

    open fun getDeviceInfo(): String {
        return ""
    }

    private fun addMap(map1: Map<String, String>, map2: Map<String, String>): MutableMap<String, String> {
        val map = map1.toMutableMap()
        for ((k, v) in map2) {
            map[k] = v
        }
        return map
    }
}