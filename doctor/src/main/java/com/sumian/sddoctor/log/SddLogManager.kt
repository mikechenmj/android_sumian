package com.sumian.sddoctor.log

import com.sumian.common.log.CommonLogManager
import com.sumian.sddoctor.app.AppManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/5 14:54
 * desc   : ref
 * https://www.tapd.cn/21254041/prong/stories/view/1121254041001003070?url_cache_key=aec350d6d1ce106539a689985e57282f&action_entry_type=stories
 * version: 1.0
 */
object SddLogManager : CommonLogManager() {

    private var mobile = ""
    private var userId = "0"

    override fun observeUserInfo() {
        AppManager.getAccountViewModel().getDoctorInfo().observeForever { t ->
            run {
                mobile = t?.mobile ?: ""
                userId = t?.id.toString()
            }
        }
    }

    override fun getAppType(): String {
        return CommonLogManager.APP_TYPE_SDD
    }

    override fun getMobile(): String {
        return mobile
    }

    override fun getUserId(): String {
        return userId
    }
}