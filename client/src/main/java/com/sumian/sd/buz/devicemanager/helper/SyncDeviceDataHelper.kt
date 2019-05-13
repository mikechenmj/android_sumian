package com.sumian.sd.buz.devicemanager.helper

import android.os.Handler
import android.os.Looper
import com.sumian.blue.model.BluePeripheral
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.buz.devicemanager.AutoSyncDeviceDataUtil
import com.sumian.sd.buz.devicemanager.DeviceManager
import com.sumian.sd.buz.devicemanager.uploadsleepdata.SleepDataUploadHelper
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.utils.StorageUtil
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/1 14:09
 * desc   :
 * version: 1.0
 */
class SyncDeviceDataHelper(deviceManager: DeviceManager) {
    companion object {
        private const val PAYLOAD_TIMEOUT_TIME = 1000L * 5
        private const val DELAY_SYNC_SUCCESS_DURATION = 1000L * 2
    }

    private val mMainHandler = Handler(Looper.getMainLooper())
    private var mPackageCurrentIndex = -1   // 透传单包进度
    private var mPackageTotalDataCount: Int = 0 // 透传单包数据总数
    private var mTotalProgress = 0         // 透传总进度
    private var mTotalDataCount: Int = 0    // 透传总数据
    private var mTotalPackageCount: Int = 0    // 透传总包数
    private var mCurrentPackageIndex: Int = 0    // 当前包
    private var mPackageNumber: Int = 1     // 透传数据 包的index
    private val m8fTransData = ArrayList<String>(0)
    private var mTranType: Int = 0
    private var mBeginCmd: String? = null
    private var mReceiveStartedTime: Long = 0
    private val mDeviceManager = deviceManager

    fun receiveRequestSleepDataResponse(cmd: String) {
        when (cmd) {
            "554f020188" -> {
                LogManager.appendTransparentLog("收到0x4f回复 发现设备有睡眠特征数据,准备同步中  cmd=$cmd")
            }
            "554f020100" -> {
                onSyncSuccess()
                LogManager.appendTransparentLog("收到0x4f回复 设备没有睡眠特征数据  cmd=$cmd")
                AutoSyncDeviceDataUtil.saveAutoSyncTime()
            }
            "554f0201ff" -> {
                onSyncFailed()
                LogManager.appendTransparentLog("收到0x4f回复 设备4f 指令识别异常  cmd=$cmd")
            }
            else -> {
            }
        }
    }

    private var mTransformStartTime = 0L;

    /**
     * 开始 55 8e 1 06a 01 5c665b03 5c5ec240 01 01 006a
     * 结束 55 8e 1 06a 0f 5c665b03
     * 55 指令头 1 byte，
     * 8e 指令类型 1 byte，
     * 1 06a 数据类型 4 bit，数据长度 12 bit，
     * 01 起始标记 1 byte，
     * 5c46833f 传输id 4 byte，
     * 386cd300 睡眠数据采集开始时间 4 byte
     * 后4byte是扩展字段
     * 01 总段数 1 byte [27-28]
     * 01 当前段 1 byte [29-30]
     * 006a 所有0x8F 类型数据包 2 byte [31-34]
     *
     */
    fun receiveStartOrFinishTransportCmd(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
        val typeAndCount = Integer.parseInt(cmd.substring(4, 8), 16)
        //16 bit 包括4bit 类型 12bit 长度 向右移12位,得到高4位的透传数据类型
        val tranType = typeAndCount shr 12
        val dataCount: Int = getDataCountFromCmd(cmd)
        LogManager.appendMonitorLog("receiveStartOrFinishTransportCmd: $cmd")
        when (data[4]) {
            0x01.toByte() //开始透传
            -> {
                mPackageCurrentIndex = -1
                m8fTransData.clear()
                mTranType = tranType
                mBeginCmd = cmd
                mReceiveStartedTime = getActionTimeInSecond()
                mPackageTotalDataCount = dataCount
                if (cmd.length == 34) {
                    mTotalPackageCount = subHexStringToInt(cmd, 26, 28)
                    mCurrentPackageIndex = subHexStringToInt(cmd, 28, 30)
                    if (mCurrentPackageIndex == 1 || mTranType == 2) { // type = 2 时 获取不到current package index
                        mTotalProgress = 0
                        mTransformStartTime = System.currentTimeMillis()
                    }
                    mTotalDataCount = subHexStringToInt(cmd, 30, 34)
                }
                LogManager.appendMonitorLog("开始透传 mCurrentPackageIndex: ${mCurrentPackageIndex}， mTotalPackageCount: $mTotalPackageCount, mTotalDataCount: $mTotalDataCount")
                if (isAvailableStorageEnough(dataCount)) {
                    writeResponse(peripheral, data, true)
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,等待设备透传 " + dataCount + "包数据" + "  cmd=" + cmd)
                    AutoSyncDeviceDataUtil.saveAutoSyncTime()
                    onSyncStart()
                    postNextPayloadTimeoutCallback()
                } else {
                    writeResponse(peripheral, data, false)
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd)
                    onSyncFailed()
                }
            }
            0x0f.toByte()// 结束。透传8f 数据接收完成,保存文件,准备上传数据到后台
            -> {
                mPackageNumber++
                @Suppress("UNCHECKED_CAST")
                if (dataCount == m8fTransData.size) {
                    val sleepData = m8fTransData.clone() as ArrayList<String>
                    m8fTransData.clear()
                    if (isAvailableStorageEnough(dataCount)) {
                        if (mTranType == 0x01) {
                            AutoSyncDeviceDataUtil.saveAutoSyncTime()
                        }
                        LogManager.appendMonitorLog("0x8e0f 透传数据" + dataCount + "包接收成功,准备写入本地文件 cmd=" + cmd)
                        SleepDataUploadHelper.getInstance()
                                .saveSleepData(sleepData, mTranType, mBeginCmd, cmd,
                                        mDeviceManager.getMonitorSn(),
                                        mDeviceManager.getSleeperSn(),
                                        mReceiveStartedTime, getActionTimeInSecond())
                        writeResponse(peripheral, data, true)
                    } else {
                        writeResponse(peripheral, data, false)
                        LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd)
                    }
                    if (mTotalDataCount != 0) { // new monitor
                        if (mTotalProgress == mTotalDataCount) {
                            StatUtil.event(StatConstants.sync_sleep_data_finish, mapOf(
                                    "duration" to (System.currentTimeMillis() - mTransformStartTime).toString(),
                                    "total_count" to mTotalDataCount.toString()
                            ))
                            LogManager.appendMonitorLog("0x8e0f 所有数据传输完毕， 共计${mTotalDataCount}条， 耗时${System.currentTimeMillis() - mTransformStartTime}")
                            onSyncSuccess()
                        }
                    } else { // old monitor
                        postDelaySyncSuccess()
                    }
                } else {
                    LogManager.appendMonitorLog(
                            "0x8e0f 透传数据" + dataCount + "包接收失败,原因是包数量不一致 实际收到包数量 RealDataCount="
                                    + mPackageCurrentIndex + " 重新透传数据已准备,等待设备重新透传  cmd=" + cmd)
                    writeResponse(peripheral, data, false)
                }
                removePayloadTimeoutCallback()
            }
            else -> {
            }
        }
    }

    fun receiveSleepData(peripheral: BluePeripheral, data: ByteArray, cmd: String) {
        if (!isSyncing()) return // 透传超时可能会走到这一行。不在透传状态不响应透传数据。
        val indexOne = Integer.parseInt(cmd.substring(4, 6), 16) and 0x0f shl 8
        val indexTwo = Integer.parseInt(cmd.substring(6, 8), 16)
        val index = indexOne + indexTwo
        if (mPackageCurrentIndex != -1 && index > mPackageCurrentIndex + 1) {
            peripheral.write(byteArrayOf(0xaa.toByte(), 0x8f.toByte(), 0x03, data[2], data[3], 0xff.toByte()))
            LogManager.appendTransparentLog("收到透传数据：cmd: $cmd，index=$index  realCount=$mPackageCurrentIndex  该index 出错,要求重传 cmd=$cmd")
        } else {
            LogManager.appendMonitorLog("收到透传数据：cmd: $cmd， index：$index, mPackageCurrentIndex:$mPackageCurrentIndex, mPackageTotalDataCount:$mPackageTotalDataCount,  mTotalProgress：${mTotalProgress}， mTotalDataCount:$mTotalDataCount")
            mTotalProgress++
            mPackageCurrentIndex = index
            peripheral.write(byteArrayOf(0xaa.toByte(), 0x8f.toByte(), 0x03, data[2], data[3], 0x88.toByte()))
            m8fTransData.add(cmd)
            if (mTotalDataCount == 0) { // old version
                onSyncProgressChange(mPackageNumber, mPackageCurrentIndex, mPackageTotalDataCount)
            } else {
                onSyncProgressChangeV2(mPackageNumber, mTotalProgress, mTotalDataCount)
            }
            postNextPayloadTimeoutCallback()
        }
    }


    private fun writeResponse(peripheral: BluePeripheral, data: ByteArray, readyForNextData: Boolean) {
        mDeviceManager.writeResponse(peripheral, data, readyForNextData)
    }

    /**
     * 获取该次透传数据总条数
     *
     * @return 透传数据总条数
     */
    private fun getDataCountFromCmd(cmd: String): Int {
        return Integer.parseInt(cmd.substring(5, 8), 16)
    }

    private fun subHexStringToInt(s: String, startIndex: Int, endIndex: Int): Int {
        return Integer.parseInt(s.substring(startIndex, endIndex), 16)
    }

    private fun getActionTimeInSecond(): Long {
        return System.currentTimeMillis() / 1000L
    }

    /**
     * dataCount + 2 表示加上了startCmd和endCmd
     * 每行命令有26个字符，加上换行，共27字符，一个字符占1byte，考虑安全性，这里计算时按30byte/cmd计算
     * 实测10000个cmd，文件273kb；
     */
    private fun isAvailableStorageEnough(dataCount: Int): Boolean {
        val dataBytes = (dataCount + 2) * 30L
        val availableExternalStorageSize = StorageUtil.getAvailableExternalStorageSize()
        return dataBytes < availableExternalStorageSize
    }

    private fun postDelaySyncSuccess() {
        removeDelaySyncSuccessRunnable()
        mMainHandler.postDelayed(mDelaySyncSuccessRunnable, DELAY_SYNC_SUCCESS_DURATION)
    }

    private fun removeDelaySyncSuccessRunnable() {
        mMainHandler.removeCallbacks(mDelaySyncSuccessRunnable)
    }

    private val mDelaySyncSuccessRunnable = Runnable { onSyncSuccess() }


    private fun postNextPayloadTimeoutCallback() {
        removePayloadTimeoutCallback()
        removeDelaySyncSuccessRunnable()
        mMainHandler.postDelayed(mPayloadTimeoutCallback, PAYLOAD_TIMEOUT_TIME)
    }

    private fun removePayloadTimeoutCallback() {
        mMainHandler.removeCallbacks(mPayloadTimeoutCallback)
    }

    private val mPayloadTimeoutCallback = Runnable {
        onSyncFailed()
    }

    private fun isSyncing(): Boolean {
        return mDeviceManager.isSyncing()
    }

    private fun onSyncStart() {
        mDeviceManager.onSyncStart()
    }

    private fun onSyncSuccess() {
        mDeviceManager.onSyncSuccess()
    }

    private fun onSyncFailed() {
        mDeviceManager.onSyncFailed()
    }

    fun onSyncProgressChange(packageNumber: Int, packageProgress: Int, packageTotalCount: Int) {
        mDeviceManager.onSyncProgressChange(packageNumber, packageProgress, packageTotalCount)
    }

    fun onSyncProgressChangeV2(packageNumber: Int, totalProgress: Int, totalCount: Int) {
        mDeviceManager.onSyncProgressChangeV2(packageNumber, totalProgress, totalCount)
    }

}