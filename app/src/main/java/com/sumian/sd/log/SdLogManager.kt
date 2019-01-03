package com.sumian.sd.log

import com.sumian.common.log.CommonLogManager
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceInfoFormatter

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 14:54
 * desc   : 日志 实现类
 * https://www.tapd.cn/21254041/prong/stories/view/1121254041001003070?url_cache_key=aec350d6d1ce106539a689985e57282f&action_entry_type=stories
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")
object SdLogManager : CommonLogManager() {
    private var mobile = ""
    private var userId = "0"

    override fun observeUserInfo() {
        AppManager.getAccountViewModel().liveDataToken.observeForever { t ->
            run {
                mobile = t?.user?.mobile ?: ""
                userId = t?.user?.id.toString()
            }
        }
    }

    override fun getAppType(): String {
        return APP_TYPE_SD
    }

    override fun getMobile(): String {
        return mobile
    }

    override fun getUserId(): String {
        return userId
    }

    override fun getDeviceInfo(): String {
        return JsonUtil.toJson(DeviceInfoFormatter.getDeviceInfoMap())
    }
}