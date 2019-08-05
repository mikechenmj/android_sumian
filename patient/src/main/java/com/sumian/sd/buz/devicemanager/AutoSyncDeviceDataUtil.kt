package com.sumian.sd.buz.devicemanager

import android.content.Context
import android.os.PowerManager
import com.blankj.utilcode.util.SPUtils
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.app.App
import com.sumian.sd.common.log.LogManager

/**
 * 用于检测 app 自动同步睡眠数据的工具类
 *
 *
 * 前置条件：

①同步中不触发自动同步

②同步成功后5s不触发自动同步

同步机制：

① 连接上监测仪时，APP从后台被唤醒

② 连接上监测仪时，APP结束锁屏状态唤醒前台APP

③ 连接上监测仪时，APP切换到【主页】tab、切换到【报告】—【监测数据】tab、切换至【我的】—【设备管理】


④ 连接上监测仪后发送事件指令
 */
object AutoSyncDeviceDataUtil {

    private const val AUTO_SYNC_KEY = "auto_sync_sleep_data_time"

    /**
     * 主动同步睡眠特征数据
     */
    fun autoSyncSleepData() {
        val powerManager = App.getAppContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive && (((System.currentTimeMillis() - getAutoSyncTime()) / 1000L) > 5)) {
            if (DeviceManager.isDeviceConnectAndCompat()) {
                LogManager.appendPhoneLog("app  主动同步睡眠数据,原因是上一次同步的时间超过5s")
                saveAutoSyncTime()
                DeviceManager.startSyncSleepDataInternal()
            }
        }
    }

    /**
     * 保存同步事件时间戳
     */
    private fun saveAutoSyncTime() {
        SPUtils.getInstance().put(AUTO_SYNC_KEY, System.currentTimeMillis())
    }

    /**
     * 获取同步事件时间戳
     */
    private fun getAutoSyncTime(): Long {
        return SPUtils.getInstance().getLong(AUTO_SYNC_KEY, 0)
    }
}