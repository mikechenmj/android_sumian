package com.sumian.sd.log

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.sumian.common.log.AliyunLogManager
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceInfoFormatter
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 14:54
 * desc   : ref
 * https://www.tapd.cn/21254041/prong/stories/view/1121254041001003070?url_cache_key=aec350d6d1ce106539a689985e57282f&action_entry_type=stories
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate")
object SdLogManager {
    public const val KEY_CLIENT_TYPE = "client_type"
    public const val KEY_NETWORK = "network"
    public const val KEY_TIME = "time"
    public const val KEY_ACTION_TYPE = "action_type"
    public const val KEY_MOBILE = "mobile"
    public const val KEY_USER_ID = "user_id"
    public const val KEY_PAGE_DATA = "page_data"
    public const val KEY_HTTP_REQUEST = "http_request"
    public const val KEY_HTTP_RESPONSE = "http_response"
    public const val KEY_APP_VERSION = "app_version"
    public const val KEY_APP_TYPE = "app_type"
    public const val KEY_DEVICE_INFO = "device_info"
    public const val KEY_REMARK = "remark"
    public const val KEY_USER_AGENT = "user_agent"

    public const val CLIENT_TYPE_H5 = "H5"
    public const val CLIENT_TYPE_ANDROID = "Android"
    public const val CLIENT_TYPE_IOS = "iOS"

    public const val APP_TYPE_SD = "sd"
    public const val APP_TYPE_SDD = "sdd"

    public const val NETWORK_TYPE_WIFI = "wifi"
    public const val NETWORK_TYPE_4G = "4G"
    public const val NETWORK_TYPE_3G = "3G"
    public const val NETWORK_TYPE_2G = "2G"
    public const val NETWORK_TYPE_UNKNOWN = "unknown"

    public const val ACTION_TYPE_PAGE = "page"
    public const val ACTION_TYPE_HTTP = "http"
    public const val ACTION_TYPE_DEVICE = "device"

    public const val PAGE_OPERATION = ""

    var clientType = CLIENT_TYPE_ANDROID
    var network = NETWORK_TYPE_UNKNOWN
    var mobile = ""
    var userId = "0"
    var appVersion = ""
    var appType = APP_TYPE_SD
    var deviceInfo = ""

    fun createTemplateLogMap(): MutableMap<String, String> {
        return mapOf(
                KEY_CLIENT_TYPE to clientType,
                KEY_NETWORK to network,
                KEY_TIME to getTime(),
                KEY_MOBILE to mobile,
                KEY_USER_ID to userId,
                KEY_APP_TYPE to appType,
                KEY_APP_VERSION to appVersion
        )
                .toMutableMap()
    }

    private fun getTime(): String {
        return Date().toString()
    }

    fun init(context: Context) {
        observeUserInfo()
        observeNetworkState(context)
        AliyunLogManager.init(context,
                BuildConfig.ALIYUN_LOG_ACCESS_KEY_ID,
                BuildConfig.ALIYUN_LOG_ACCESS_SECRET,
                BuildConfig.ALIYUN_LOG_PROJECT,
                BuildConfig.ALIYUN_LOG_LOG_STORE,
                BuildConfig.ALIYUN_LOG_END_POINT
        )
        network = getNetworkTypeString()
        appVersion = AppUtils.getAppVersionName()
        deviceInfo = JsonUtil.toJson(DeviceInfoFormatter.getDeviceInfoMap())
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

    private fun observeUserInfo() {
        AppManager.getAccountViewModel().liveDataToken.observeForever { t ->
            run {
                mobile = t?.user?.mobile ?: ""
                userId = t?.user?.id.toString() ?: "0"
                LogUtils.d("token", t.toString())
            }
        }
    }

    private fun addMap(map1: Map<String, String>, map2: Map<String, String>): MutableMap<String, String> {
        val map = map1.toMutableMap()
        for ((k, v) in map2) {
            map[k] = v
        }
        return map
    }
}