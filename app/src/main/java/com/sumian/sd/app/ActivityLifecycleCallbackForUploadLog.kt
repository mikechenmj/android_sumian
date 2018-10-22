package com.sumian.sd.app

import android.app.Activity
import com.blankj.utilcode.util.AppUtils
import com.sumian.common.lifecycle.EmptyActivityLifecycleCallbacks
import com.sumian.hw.log.LogJobIntentService
import com.sumian.hw.log.LogManager

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/27 10:46
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ActivityLifecycleCallbackForUploadLog : EmptyActivityLifecycleCallbacks() {
    private var mIsForeground = false

    override fun onActivityStarted(activity: Activity?) {
        super.onActivityStarted(activity)
        if (!mIsForeground) {
            LogManager.appendPhoneLog("APP 切换到 前台")
        }
        mIsForeground = true
    }

    override fun onActivityStopped(activity: Activity?) {
        if (!AppUtils.isAppForeground()) {
            mIsForeground = false
            LogManager.appendPhoneLog("APP 切换到 后台")
        }
        LogJobIntentService.uploadLogIfNeed(activity)
    }
}