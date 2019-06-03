package com.sumian.device.manager.helper

import android.os.Handler
import android.os.Looper
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.cmd.BleCmd
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.upload.SleepDataManager
import com.sumian.device.util.BleCmdUtil
import com.sumian.device.util.LogManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 18:15
 * desc   :
 * version: 1.0
 */
object SyncSleepDataHelper {
    private var mIsSyncing = false
    private const val PAYLOAD_TIMEOUT_TIME = 1000L * 5
    private const val TAG = "[SyncSleepData]"
    private const val SYNC_TYPE_SLEEP_DATA = 1
    private const val SYNC_TYPE_SLEEP_MASTER_LOG = 2

    private val mMainHandler = Handler(Looper.getMainLooper())
    private var mPackageTotalDataCount: Int = 0 // 透传单段数据总数
    private var mCurrentPackageProgress = 0         // 透传当前package进度
    private var mProgress = 0         // 透传总进度
    private var mTotalDataCount: Int = 0    // 透传总数据
    private var mTotalPackageCount: Int = 0    // 透传总段数
    private var mCurrentPackageIndex: Int = 0    // 当前段
    private var mTransData = arrayOfNulls<String?>(0)
    private var mTransDataId: String? = null
    private var mTranType: Int = 0
    private var mBeginCmd: String? = null
    private var mEndCmd: String? = null
    private var mBeginBytes: ByteArray? = null
    private var mEndBytes: ByteArray? = null
    private var mReceiveStartedTime = 0
    private var mIsGettingLostFrame = false
    private var mLostFrameIndexList = LinkedHashSet<Int>()
    private val mBleCommunicationWatcher = object : BleCommunicationWatcher {
        override fun onRead(data: ByteArray, hexString: String) {
            if (!BleCmdUtil.isDeviceCmdValid(hexString)) {
                return
            }
            when (BleCmdUtil.getCmdType(hexString)) {
                "4f" -> {
                    // 主动获取睡眠特征数据response
                    receiveRequestSleepDataResponse(hexString)
                }
                "8e" -> {
                    // 透传数据 开始/结束
                    receiveStartOrFinishTransportCmd(data, hexString)
                }
                "8f" -> {
                    // 透传数据 帧
                    receiveSleepData(data, hexString)
                }
                else -> {
                }
            }
        }
    }

    fun init() {
        DeviceManager.registerBleCommunicationWatcher(mBleCommunicationWatcher)
    }


    fun startSyncSleepData() {
        DeviceManager.writeData(BleCmdUtil.createDataFromString(BleCmd.SYNC_DATA, "01"))
    }


    private fun startSyncSleepMasterLog() {
        DeviceManager.writeData(BleCmdUtil.createDataFromString(BleCmd.SYNC_DATA, "02"))
    }

    /**
     * 554f020188
     * 554f020100
     * 554f0201ff
     */
    fun receiveRequestSleepDataResponse(cmd: String) {
        when (cmd.substring(cmd.length - 2)) {
            "88" -> {
                log("收到0x4f回复 发现设备有睡眠特征数据,准备同步中  cmd=$cmd")
            }
            "00" -> {
                onSyncSuccess()
                log("收到0x4f回复 设备没有睡眠特征数据  cmd=$cmd")
            }
            "ff" -> {
                onSyncFailed()
                log("收到0x4f回复 设备4f 指令识别异常  cmd=$cmd")
            }
            else -> {
            }
        }
    }

    /**
     * 开始
     *  55 8e A BBB CC DDDDDDDD EEEEEEEE FF GG HHHH
     *  55 8e 1 06a 01 5c665b03 5c5ec240 01 01 006a
     *  55 8e 2 02e 01 4b3cce28 4b3cce28 01 01 002e
     *
     * 结束
     * 55 8e 1 06a 0f 5c665b03
     * 55 8e 1 06a 0f 5c665b03
     *
     * A 数据类型 ，1 睡眠特征， 2 速眠仪日志
     * B 当前段数据总数
     * C 01：当前段发送开始，0f 当前段发送结束
     * D 传输开始时间，
     * E 当前段睡眠数据采集开始时间
     * F 总段数
     * G 当前段
     * H 所有0x8F 类型数据包 2 byte [31-34]
     *
     */
    fun receiveStartOrFinishTransportCmd(data: ByteArray, cmd: String) {
        when (data[4]) {
            //开始透传
            0x01.toByte() -> onReceiveSleepDataStart(cmd, data)
            // 结束。透传8f 数据接收完成,保存文件,准备上传数据到后台
            0x0f.toByte() -> onReceiveSleepDataEnd(cmd, data)
            else -> {
            }
        }
    }

    private fun onReceiveSleepDataStart(cmd: String, data: ByteArray) {
        log("on sync start: $cmd")
        val typeAndCount = Integer.parseInt(cmd.substring(4, 8), 16)
        //16 bit 包括4bit 类型 12bit 长度 向右移12位,得到高4位的透传数据类型
        val tranType = cmd.substring(4, 5).toInt()
        val dataCount: Int = subHexStringToInt(cmd, 5, 8)
        mTransData = arrayOfNulls(dataCount)
        mTranType = tranType
        mBeginCmd = cmd
        mTransDataId = cmd.substring(10, 18)
        mBeginBytes = data
        mReceiveStartedTime = getCurrentTimeInSecond()
        mPackageTotalDataCount = dataCount
        mTotalPackageCount = subHexStringToInt(cmd, 26, 28)
        mCurrentPackageIndex = subHexStringToInt(cmd, 28, 30)
        mCurrentPackageProgress = 0
        if (mCurrentPackageIndex == 1) {
            mProgress = 0
        }
        mTotalDataCount = subHexStringToInt(cmd, 30, 34)
        log("开始透传 mCurrentPackageIndex: ${mCurrentPackageIndex}， mTotalPackageCount: $mTotalPackageCount, mTotalDataCount: $mTotalDataCount")
        onSyncStart()
        postNextPayloadTimeoutCallback()
        writeResponse(mBeginBytes!!, 0x88)
        mIsGettingLostFrame = false
    }

    private fun onReceiveSleepDataEnd(cmd: String, data: ByteArray) {
        log("on sync end: $cmd")
        mEndCmd = cmd
        mEndBytes = data
        calLostFrames()
        writeResponse(data, 0x01)
        if (hasLostFrames()) {
            mIsGettingLostFrame = true
            requestNextLostFrame()
            postNextPayloadTimeoutCallback()
        } else {
            onCurrentPackageReceivedSuccess()
        }
    }


    /**
     * 558f A BBB CCCCCCCCCCCCCCCCCC
     * A 数据类型 1 睡眠特征， 2 速眠仪日志
     * BBB index
     * CCC 数据内容
     */
    fun receiveSleepData(data: ByteArray, cmd: String) {
        if (!isSyncing()) return // 透传超时可能会走到这一行。不在透传状态不响应透传数据。
        val index = BleCmdUtil.hexStringToLong(cmd.substring(5, 8)).toInt()
        log("收到透传数据：cmd: $cmd， index：$index, currentPackage: $mCurrentPackageProgress / $mPackageTotalDataCount,  total: $mProgress / $mTotalDataCount")
        if (mTransData[index] == null) {
            mProgress++
            mCurrentPackageProgress++
        }
        mTransData[index] = cmd
        onSyncProgressChange(mProgress, mTotalDataCount)
        postNextPayloadTimeoutCallback()
        if (mLostFrameIndexList.contains(index)) {
            mLostFrameIndexList.remove(index)
        }
        if (mIsGettingLostFrame) {
            if (hasLostFrames()) {
                requestNextLostFrame()
            } else {
                onCurrentPackageReceivedSuccess()
            }
        }
    }

    private fun onCurrentPackageReceivedSuccess() {
        saveSleepDataToFile()
        writeResponse(mEndBytes!!, 0x88)
        if (mCurrentPackageIndex == mTotalPackageCount) {
            removePayloadTimeoutCallback()
            onSyncSuccess()
        } else {
            postNextPayloadTimeoutCallback()
        }
    }

    private fun hasLostFrames() = mLostFrameIndexList.size > 0

    /**
     * cal lost frames
     */
    private fun calLostFrames() {
        mLostFrameIndexList.clear()
        for (i in 0 until mTransData.size) {
            if (mTransData[i] == null) {
                mLostFrameIndexList.add(i)
            }
        }
    }

    private fun saveSleepDataToFile() {
        val sleepData = ArrayList<String?>()
        sleepData.add(mBeginCmd)
        sleepData.addAll(mTransData.toMutableList())
        sleepData.add(mEndCmd)
        log("0x8e0f 透传数据" + mTransData.size + "包接收成功,准备写入本地文件 cmd=" + mEndCmd)
        SleepDataManager
                .saveAndUploadData(
                        DeviceManager.mApplication,
                        sleepData,
                        mTransDataId!!,
                        mTranType,
                        DeviceManager.getDevice()?.monitorSn,
                        DeviceManager.getDevice()?.sleepMasterSn,
                        mReceiveStartedTime,
                        getCurrentTimeInSecond()
                )
    }

    private fun requestNextLostFrame() {
        val lostFrameIndex = mLostFrameIndexList.first()
        log("请求丢帧数据： $lostFrameIndex / $mLostFrameIndexList")
        val bytes = transfer4bit12bitIntsToBytes(mTranType, lostFrameIndex)
        peripheralWrite(
                byteArrayOf(
                        0xaa.toByte(),
                        0x8f.toByte(),
                        0x03,
                        bytes[0],
                        bytes[1],
                        0xff.toByte()
                )
        )
    }

    fun peripheralWrite(data: ByteArray) {
        DeviceManager.writeData(data)
    }

    /**
     * @param data             data
     * @param resp ff：异常，88：准备接收下一段, 01 当前段收到
     *
     * 格式： aa 8e 1 06a 01 5c665b03 ff
     * 示例： AA BB C DDD EE FFFFFFFF GG
     * AA 指令头 1 byte，[0-1]
     * BB 指令类型 1 byte，[2-3]
     * C 数据类型 4 bit，[4-4]
     * DDD 数据长度 12 bit，[5-7]
     * EE 起始标记, 01开始，0f结束 1 byte，[8-9]
     * FFFFFFFF 传输id 4 byte，[10-17]
     * GG 应答 ff：异常，88：准备接收下一段, 01 当前段收到
     */
    private fun writeResponse(data: ByteArray, resp: Int) {
        val command = byteArrayOf(
                0xaa.toByte(), 0x8e.toByte(),
                data[2], data[3], data[4], data[5], data[6], data[7], data[8], resp.toByte()
        )
        peripheralWrite(command)
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

    private fun getCurrentTimeInSecond(): Int {
        return (System.currentTimeMillis() / 1000L).toInt()
    }

    private fun postNextPayloadTimeoutCallback() {
        removePayloadTimeoutCallback()
        mMainHandler.postDelayed(mPayloadTimeoutCallback, PAYLOAD_TIMEOUT_TIME)
    }

    private fun removePayloadTimeoutCallback() {
        mMainHandler.removeCallbacks(mPayloadTimeoutCallback)
    }

    private val mPayloadTimeoutCallback = Runnable {
        onSyncFailed()
    }

    fun isSyncing(): Boolean {
        return mIsSyncing
    }

    private fun onSyncStart() {
        mIsSyncing = true
        if (isSyncSleepData()) DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_START, null)
    }

    private fun onSyncSuccess() {
        mIsSyncing = false
        if (isSyncSleepData()) {
            DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS, null)
            startSyncSleepMasterLog()
        }
    }

    private fun onSyncFailed() {
        mIsSyncing = false
        if (isSyncSleepData()) DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL, null)
    }

    fun onSyncProgressChange(progress: Int, totalCount: Int) {
        if (isSyncSleepData()) {
            DeviceManager.postEvent(
                    DeviceManager.EVENT_SYNC_SLEEP_DATA_SYNC_PROGRESS_CHANGE,
                    intArrayOf(progress, totalCount)
            )
        }
    }

    private fun isSyncSleepData() = mTranType == SYNC_TYPE_SLEEP_DATA

    /**
     * 2byte 前4bit，后12bit转换为2个数字
     */
    @ExperimentalUnsignedTypes
    private fun transfer4bit12bitBytesToInts(highByte: Byte, lowByte: Byte): IntArray {
        val high = highByte.toInt() and 0xFF // and 0xFF 处理无符号byte（-128 ->128）
        val low = lowByte.toInt() and 0xFF
        return intArrayOf(high shr 4, (high and 15) * 256 + low)
    }

    /**
     * 4bit, 12bit 的数字转换为2byte
     */
    private fun transfer4bit12bitIntsToBytes(_4bit: Int, _12bit: Int): Array<Byte> {
        return arrayOf(((_4bit shl 4) + _12bit / 256).toByte(), (_12bit % (256)).toByte())
    }

    private fun log(s: String) {
        LogManager.transparentLog(TAG + s)
    }

    fun getSyncSleepDataProgress(): Int {
        return mProgress
    }

    fun getSyncSleepDataTotalCount(): Int {
        return mTotalDataCount
    }

}