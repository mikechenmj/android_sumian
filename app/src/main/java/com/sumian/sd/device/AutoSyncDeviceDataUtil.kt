package com.sumian.sd.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import com.sumian.hw.log.LogManager
import com.sumian.hw.utils.SpUtil
import com.sumian.sd.app.App

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


    private const val AUTO_SYNC_FILE_NAME = "upload_sleep_cha_time"
    private const val AUTO_SYNC_KEY = "time"

    private val mGlobalReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> LogManager.appendUserOperationLog("手机被用户锁屏,熄灭屏幕")
                    Intent.ACTION_SCREEN_ON -> LogManager.appendUserOperationLog("手机被用户点亮屏幕")
                    Intent.ACTION_USER_PRESENT -> {
                        LogManager.appendUserOperationLog("app  从后台切换到前台,并且屏幕被用户解锁了")
                        autoSyncSleepData()
                    }
                }
            }
        }
    }

    /**
     * 注册监听,注册成功之后,主动检测一次,如果符合条件,主动同步一次数据特征数据
     */
    fun register(context: Context) {
        val globalFilter = IntentFilter()
        globalFilter.addAction(Intent.ACTION_USER_FOREGROUND)
        globalFilter.addAction(Intent.ACTION_USER_BACKGROUND)
        globalFilter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(mGlobalReceiver, globalFilter)
        autoSyncSleepData()//主动自动同步
    }

    /**
     * 解注册监听
     */
    fun unRegister(context: Context) {
        context.unregisterReceiver(mGlobalReceiver)
    }

    /**
     * 主动同步睡眠特征数据
     */
    fun autoSyncSleepData() {
        val powerManager = App.getAppContext().getSystemService(Context.POWER_SERVICE) as PowerManager

        if (DeviceManager.isConnected() && powerManager.isInteractive && (getAutoSyncTime() == 0L || ((System.currentTimeMillis() - getAutoSyncTime()) / 1000L) > 5)) {
            LogManager.appendPhoneLog("app  主动同步睡眠数据,原因是上一次同步的时间超过5s")
            DeviceManager.syncSleepData()
        }
    }

    /**
     * 保存同步事件时间戳
     */
    fun saveAutoSyncTime() {
        SpUtil.initEdit(AUTO_SYNC_FILE_NAME).putLong(AUTO_SYNC_KEY, System.currentTimeMillis()).apply()
    }

    /**
     * 获取同步事件时间戳
     */
    private fun getAutoSyncTime(): Long {
        return SpUtil.initSp(AUTO_SYNC_FILE_NAME).getLong(AUTO_SYNC_KEY, 0)
    }
}