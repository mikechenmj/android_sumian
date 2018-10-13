package com.sumian.sd.device

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 11:31
 * desc   :
 * version: 1.0
 */
interface MonitorEventListener {
    fun onSyncStart()

    /**
     * @param packageNumber   第几个数据包
     * @param progress 同步进度
     * @param total           当前包数据总量
     */
    fun onSyncProgressChange(packageNumber: Int, progress: Int, total: Int)

    fun onSyncSuccess()

    fun onSyncFailed()

    fun onTurnOnPaModeStart()

    fun onTurnOnPaModeSuccess()

    fun onTurnOnPaModeFailed(message: String)
}