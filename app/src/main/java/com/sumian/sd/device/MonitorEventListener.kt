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
     * @param packageProgress 当前包同步进度
     * @param packageTotalCount    当前包数据总量
     */
    fun onSyncProgressChange(packageNumber: Int, packageProgress: Int, packageTotalCount: Int)

    /**
     * @param packageNumber   第几个数据包
     * @param totalProgress 所有包同步进度
     * @param totalCount    所有包数据总量
     */
    fun onSyncProgressChangeV2(packageNumber: Int, totalProgress: Int, totalCount: Int)

    fun onSyncSuccess()

    fun onSyncFailed()

    fun onTurnOnPaModeStart()

    fun onTurnOnPaModeSuccess()

    fun onTurnOnPaModeFailed(message: String)

    fun onConnectStart()

    fun onConnectFailed()

    fun onConnectSuccess()
}