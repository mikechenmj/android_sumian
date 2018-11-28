package com.sumian.sd.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.sumian.hw.log.LogManager
import com.sumian.sd.device.DeviceManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/15 15:38
 * desc   :
 * version: 1.0
 */
class ForegroundBackgroundListener : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        LogManager.appendUserOperationLog("App 进入 前台")
        DeviceManager.tryToConnectCacheMonitor()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        LogManager.appendUserOperationLog("App 进入 后台")
    }
}