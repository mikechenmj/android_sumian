package com.sumian.device.manager.helper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.clj.fastble.utils.HexUtil
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.callback.WriteBleDataCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.upload.SleepDataUploadManager
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
    private const val PAYLOAD_TIMEOUT_TIME = 1000L * 8
    private const val TAG = "[SyncSleepData]"
    private const val SYNC_TYPE_SLEEP_DATA = 1
    private const val SYNC_TYPE_SLEEP_MASTER_LOG = 2

    private var mPackageTotalDataCount: Int = 0 // 透传单段数据总数
    private var mCurrentPackageProgress = 0         // 透传当前package进度
    private var mProgress = 0         // 透传总进度
    private var mTotalDataCount: Int = 0    // 透传总数据
    private var mTotalPackageCount: Int = 0    // 透传总段数
    private var mCurrentPackageIndex: Int = 0    // 当前段
    private var mTransData = arrayOfNulls<String?>(0)
    private var mTransDataId: String? = null
    private var mTranType: Int = SYNC_TYPE_SLEEP_DATA
    private var mIsSleepDataTypeSyncing = false
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
                BleCmd.SYNC_DATA -> {
                    // 主动获取睡眠特征数据response
                    receiveRequestSleepDataResponse(hexString)
                }
                BleCmd.SYNC_START_OR_END -> {
                    // 透传数据 开始/结束
                    receiveStartOrFinishTransportCmd(data, hexString)
                }
                BleCmd.SYNC_TRANSPARENT -> {
                    // 透传数据 帧
                    receiveSleepData(data, hexString)
                }
                else -> {
                }
            }
        }
    }

    private const val MESSAGE_TAG_SET_SYNC_FALSE = "set_sync_flag_false_tag"
    private const val MESSAGE_DELAY_SET_SYNC_FALSE = 8000L
    private const val MESSAGE_CODE_SET_SYNC_FALSE = 9
    private const val MESSAGE_CODE_NEXT_PAYLOAD = 10

    private var mMainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MESSAGE_CODE_SET_SYNC_FALSE -> {
                    var tag = msg?.data?.getString(MESSAGE_TAG_SET_SYNC_FALSE) ?: null
                    setIsSyncing(false, "$tag send MESSAGE_CODE_SET_SYNC_FALSE and")
                }
                MESSAGE_CODE_NEXT_PAYLOAD -> {
                    var type = msg.arg1
                    var beginCmd = msg.obj as String
                    onSyncTimeOut(type, beginCmd)
                }
            }
        }
    }

    private fun sendSetSyncFlagFalseMessageDelay(tag: String? = null) {
        removeSetSyncFlagFalseMessage("$tag sendSetSyncFlagFalseMessageDelay")
        var message = Message.obtain()
        message.what = MESSAGE_CODE_SET_SYNC_FALSE
        message.arg1 = mTranType
        if (tag != null) {
            var data = Bundle()
            data.putString(MESSAGE_TAG_SET_SYNC_FALSE, tag)
            message.data = data
        }
        mMainHandler.sendMessageDelayed(message, MESSAGE_DELAY_SET_SYNC_FALSE)
    }

    private fun removeSetSyncFlagFalseMessage(tag: String? = null) {
        bleFlowLog("$tag removeSetSyncFlagFalseMessage")
        mMainHandler.removeMessages(MESSAGE_CODE_SET_SYNC_FALSE)
    }

    fun init() {
        DeviceManager.registerBleCommunicationWatcher(mBleCommunicationWatcher)
        SleepDataUploadManager.setUploadListener(object : SleepDataUploadManager.UploadListener {
            override fun onAllFileUploaded() {
                DeviceManager.postEvent(DeviceManager.EVENT_ALL_SLEEP_DATA_UPLOADED)
            }
        })
    }


    fun startSyncSleepData(): Boolean {
        if (isSyncing()) {
            return false
        }
        setIsSyncing(true, "startSyncSleepData")
        sendSetSyncFlagFalseMessageDelay("startSyncSleepData")
        DeviceManager.writeData(BleCmdUtil.createDataFromString(BleCmd.SYNC_DATA, BleCmd.SYNC_SLEEP_DATA_CONTENT))
        return true
    }

    private fun startSyncSleepMasterLog() {
        DeviceManager.writeData(BleCmdUtil.createDataFromString(BleCmd.SYNC_DATA, BleCmd.SYNC_SLEEP_MASTER_LOG_CONTENT))
    }

    /**
     * 55 4f 02 01 88
     * 55 4f 02 01 00
     * 55 4f 02 01 ff
     */
    fun receiveRequestSleepDataResponse(cmd: String) {
        bleFlowLog("SYNC_DATA: $cmd")
        mTranType = Integer.parseInt(cmd.substring(6, 8), 16)
        when (cmd.substring(cmd.length - 2)) {
            BleCmd.RESPONSE_CODE_SUCCESS -> {
                setIsSyncing(true, "RESPONSE_CODE_SUCCESS")
                log("收到0x4f回复 发现设备有睡眠特征数据,准备同步中  cmd=$cmd")
            }
            BleCmd.RESPONSE_CODE_NONE -> {
                onSyncSuccess()
                log("收到0x4f回复 设备没有睡眠特征数据  cmd=$cmd")
            }
            BleCmd.RESPONSE_CODE_FAIL -> {
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
            0x01.toByte() -> {
                onReceiveSleepDataStart(cmd, data)
            }
            // 结束。透传8f 数据接收完成,保存文件,准备上传数据到后台
            0x0f.toByte() -> {
                onReceiveSleepDataEnd(cmd, data)
            }
            else -> {
                bleFlowLog("SYNC_START_OR_END_PARSE_FAIL: $cmd")
            }
        }
    }

    private fun onReceiveSleepDataStart(cmd: String, data: ByteArray) {
        bleFlowLog("SYNC_START: $cmd")
        if (!DeviceManager.isDeviceVersionCompatForSyncingData()) {
            writeResponse(data, BleCmd.RESPONSE_CODE_FAIL)
            bleFlowLog("!isDeviceVersionCompatForSyncingData and writeResponse RESPONSE_CODE_FAIL for $cmd")
            onSyncFailed()
            return
        }
        val dataCount: Int = subHexStringToInt(cmd, 5, 8)
        mTransData = arrayOfNulls(dataCount)
        mTranType = cmd.substring(4, 5).toInt()
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
        sendNextPayloadTimeoutDelay()
        writeResponse(mBeginBytes!!, BleCmd.RESPONSE_CODE_SUCCESS)
        bleFlowLog("writeResponse RESPONSE_CODE_SUCCESS for $cmd")
        mIsGettingLostFrame = false
    }

    private fun onReceiveSleepDataEnd(cmd: String, data: ByteArray) {
        bleFlowLog("SYNC_END: $cmd")
        if (!isSyncing()) {
            bleFlowLog("!isSyncing() and writeResponse RESPONSE_CODE_FAIL for $cmd")
            return
        }
        mEndCmd = cmd
        mEndBytes = data
        calLostFrames()
        bleFlowLog("writeResponse RESPONSE_CODE_POSITIVE for $cmd")
        writeResponse(data, BleCmd.RESPONSE_CODE_POSITIVE, object : WriteBleDataCallback {
            override fun onSuccess(data: ByteArray) {
                bleFlowLog("writeResponse RESPONSE_CODE_POSITIVE success for $cmd")
                if (hasLostFrames()) {
                    mIsGettingLostFrame = true
                    requestNextLostFrame()
                    sendNextPayloadTimeoutDelay()
                } else {
                    onCurrentPackageReceivedSuccess()
                }
            }

            override fun onFail(code: Int, msg: String) {
            }

        })
    }


    /**
     * 558f A BBB CCCCCCCCCCCCCCCCCC
     * A 数据类型 1 睡眠特征， 2 速眠仪日志
     * BBB index
     * CCC 数据内容
     */
    fun receiveSleepData(data: ByteArray, cmd: String) {
        // 透传超时可能会走到这一行。不在透传状态不响应透传数据。
        if (!isSyncing()) {
            bleFlowLog("已不在同步状态，不接受数据 $cmd")
            return
        }
        val index = BleCmdUtil.hexStringToLong(cmd.substring(5, 8)).toInt()
        if (index == 0) {
            bleFlowLog("SYNC_TRANSPARENT receiveSleepData start: $cmd")
        }
        if (index == mTransData.size - 1) {
            bleFlowLog("SYNC_TRANSPARENT receiveSleepData end: $cmd")
        }
        log("收到透传数据：cmd: $cmd， index：$index, currentPackage: $mCurrentPackageProgress / $mPackageTotalDataCount,  total: $mProgress / $mTotalDataCount")
        if (mTransData[index] == null) {
            mProgress++
            mCurrentPackageProgress++
        }
        mTransData[index] = cmd
        onSyncProgressChange(mProgress, mTotalDataCount)
        sendNextPayloadTimeoutDelay()
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
        writeResponse(mEndBytes!!, BleCmd.RESPONSE_CODE_SUCCESS)
        bleFlowLog("writeResponse RESPONSE_CODE_SUCCESS for ${HexUtil.formatHexString(mEndBytes)}")
        if (mCurrentPackageIndex == mTotalPackageCount) {
            removePayloadTimeoutMessage()
            onSyncSuccess()
        } else {
            sendNextPayloadTimeoutDelay()
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
        SleepDataUploadManager
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

    fun peripheralWrite(data: ByteArray, callback: WriteBleDataCallback? = null) {
        DeviceManager.writeData(data, 0, callback)
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
    private fun writeResponse(data: ByteArray, resp: String, callback: WriteBleDataCallback? = null) {
        val command = byteArrayOf(
                0xaa.toByte(), 0x8e.toByte(),
                data[2], data[3], data[4], data[5], data[6], data[7], data[8], Integer.parseInt(resp, 16).toByte()
        )
        peripheralWrite(command, callback)
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

    private fun sendNextPayloadTimeoutDelay() {
        removePayloadTimeoutMessage()
        var message = Message.obtain()
        message.what = MESSAGE_CODE_NEXT_PAYLOAD
        message.arg1 = mTranType
        message.obj = mBeginCmd
        mMainHandler.sendMessageDelayed(message, PAYLOAD_TIMEOUT_TIME)
    }

    private fun removePayloadTimeoutMessage() {
        mMainHandler.removeMessages(MESSAGE_CODE_NEXT_PAYLOAD)
    }

    fun isSyncing(): Boolean {
        return mIsSyncing
    }

    fun isSleepDataTypeSyncing(): Boolean {
        bleFlowLog("isSleepDataTypeSyncing: $mIsSleepDataTypeSyncing")
        return mIsSleepDataTypeSyncing
    }

    fun setIsSleepDataTypeSyncing(isSyncing: Boolean) {
        bleFlowLog("setIsSleepDataTypeSyncing: $isSyncing")
        mIsSleepDataTypeSyncing = isSyncing
    }

    private fun onSyncStart() {
        setIsSyncing(true, "onSyncStart")
        if (isSyncSleepData()) {
            setIsSleepDataTypeSyncing(true)
            DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_START, null)
        }
    }

    private fun onSyncSuccess() {
        sendSetSyncFlagFalseMessageDelay("onSyncSuccess")
        if (isSyncSleepData()) {
            setIsSleepDataTypeSyncing(false)
            DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_SUCCESS, null)
        }
    }

    private fun onSyncFailed() {
        setIsSyncing(false, "onSyncFailed")
        if (isSyncSleepData()) {
            setIsSleepDataTypeSyncing(false)
            DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL, null)
        }
    }

    private fun onSyncTimeOut(type: Int, beginCmd: String) {
        bleFlowLog("透传数据超时 type: $type beginCmd: $beginCmd")
        setIsSyncing(false, "onSyncTimeOut")
        if (type == SYNC_TYPE_SLEEP_DATA) {
            setIsSleepDataTypeSyncing(false)
            DeviceManager.postEvent(DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL, null)
        }
    }

    private fun setIsSyncing(isSyncing: Boolean, tag: String? = null) {
        if (isSyncing) {
            if (tag != null) {
                removeSetSyncFlagFalseMessage("$tag setIsSyncing $isSyncing")
            }
        } else {
            if (tag != null) {
                bleFlowLog("$tag set sync flag to $isSyncing")
            }
        }
        mIsSyncing = isSyncing

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

    private fun bleFlowLog(s: String) {
        LogManager.bleFlowLog(s)
    }

    fun getSyncSleepDataProgress(): Int {
        return mProgress
    }

    fun getSyncSleepDataTotalCount(): Int {
        return mTotalDataCount
    }

    enum class SyncState {
        FAIL_IS_SYNCING, FAIL_VERSION_WRONG, START, RETRY
    }
}