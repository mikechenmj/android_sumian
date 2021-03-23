package com.sumian.devicedemo.dfuDemo.updater

import android.bluetooth.BluetoothGatt
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.sumian.devicedemo.dfuDemo.*
import com.sumian.devicedemo.dfuDemo.updater.bean.SelectCmdResult
import com.sumian.devicedemo.dfuDemo.updater.bean.CheckSumResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.zip.CRC32
import kotlin.math.ceil

abstract class DfuPackageUpdater(protected val dfuPackage: ByteArray, private val deviceId: String) {

    private var mResponseChannel: Channel<in Any?> = Channel(Channel.CONFLATED)
    private var mPRNChannel: Channel<CheckSumResult> = Channel(Channel.CONFLATED)
    private var mChecksumChannel: Channel<CheckSumResult> = Channel(Channel.CONFLATED)
    private lateinit var mDevice: BleDevice
    private var mCrc = CRC32()
    private lateinit var mSelectCmdResult: SelectCmdResult
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    abstract fun getType(): Int

    abstract fun getPRNNumber(): Int

    suspend fun startUpdate(force: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            mDevice = connect()
            if (!force && startNotify() != CHANNEL_SUCCESS) {
                return@withContext false
            }
            val selectResult = sendSelect().log("select 指令回复内容: ") ?: return@withContext false
            mSelectCmdResult = selectResult
            if (!cmdRetry { sendPRN().negativeLog("发送 PRN失败") }) {
                return@withContext false
            }
            return@withContext writePackage(selectResult, force).negativeLog("发送包内容失败")
        }
    }

    protected suspend fun connect(): BleDevice {
        val channel = Channel<BleDevice>(Channel.CONFLATED)
        var retryTimes = 0
        val callback = object : BleGattCallback() {
            override fun onStartConnect() {
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                if (retryTimes < RETRY_MAX) {
                    BleManager.getInstance().connect(deviceId, this)
                } else {
                    Log.i("MCJ", "连接设备: $deviceId 失败")
                }
                retryTimes += 1
            }

            override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                coroutineScope.launch {
                    channel.send(bleDevice!!)
                    channel.close()
                    Log.i("MCJ", "连接设备: $deviceId 成功")
                }
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
            }
        }
        BleManager.getInstance().connect(deviceId, callback)
        return channel.receive()
    }

    private suspend fun startNotify(): Int {
        val channel = Channel<Int>(Channel.CONFLATED)
        BleManager.getInstance().notify(
                mDevice,
                DFU_SERVICE_UUID,
                CONTROL_POINT_UUID,
                object : BleNotifyCallback() {
                    override fun onCharacteristicChanged(data: ByteArray?) {
                        onDfuCharacteristicChanged(data)
                    }

                    override fun onNotifyFailure(exception: BleException?) {
                        Log.i("MCJ", "启用 notify 功能失败 ${exception?.description}")
                        coroutineScope.launch {
                            channel.send(CHANNEL_FAILED)
                            channel.close()
                        }
                    }

                    override fun onNotifySuccess() {
                        coroutineScope.launch {
                            channel.send(CHANNEL_SUCCESS)
                            channel.close()
                            Log.i("MCJ", "启用 notify 功能成功")
                        }
                    }
                })
        return channel.receive()
    }

    private suspend fun writeControlPointData(device: BleDevice, cmd: ByteArray): Boolean {
        val channel = Channel<Boolean>(Channel.CONFLATED)
        var retryTimes = 0
        val callback: BleWriteCallback = object : BleWriteCallback() {
            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                coroutineScope.launch {
                    channel.send(true)
                    channel.close()
                }
            }

            override fun onWriteFailure(exception: BleException?) {
                if (retryTimes < RETRY_MAX) {
                    BleManager.getInstance().write(device, DFU_SERVICE_UUID, CONTROL_POINT_UUID, cmd, this)
                } else {
                    coroutineScope.launch {
                        channel.send(false)
                        channel.close()
                    }
                    Log.i("MCJ", "onWriteFailure")
                }
                retryTimes += 1
            }

        }
        BleManager.getInstance().write(device, DFU_SERVICE_UUID, CONTROL_POINT_UUID, cmd, callback)
        return channel.receive()
    }

    private suspend fun writeBluetooth(device: BleDevice, cmd: ByteArray): Boolean {
        val channel = Channel<Boolean>(Channel.CONFLATED)
        var retryTimes = 0
        val callback: BleWriteCallback = object : BleWriteCallback() {
            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                coroutineScope.launch {
                    channel.send(true)
                    channel.close()
                }
            }

            override fun onWriteFailure(exception: BleException?) {
                if (retryTimes < RETRY_MAX) {
                    Log.i("MCJ", "$retryTimes onWriteFailure: ${exception?.description}")
                    BleManager.getInstance().write(device, DFU_SERVICE_UUID, PACKET_UUID, cmd, this)
                } else {
                    coroutineScope.launch {
                        channel.send(false)
                        channel.close()
                    }
                    Log.i("MCJ", "onWriteFailure")
                }
                retryTimes += 1
            }

        }
        BleManager.getInstance().write(device, DFU_SERVICE_UUID, PACKET_UUID, cmd, callback)
        return channel.receive()
    }

    private suspend fun sendSelect(): SelectCmdResult? {
        return withTimeoutOrNull(5000) {
            val cmd = byteArrayOf(CMD_SELECT.toByte(), getType().toByte())
            if (writeControlPointData(mDevice, cmd)) {
                mResponseChannel.receive() as SelectCmdResult?
            } else {
                null
            }
        }
    }

    private suspend fun sendPRN(): Boolean {
        return withTimeoutOrNull(5000) {
            val valuePRN = HexUtil.hexStringToBytes(String.format("%04x", getPRNNumber()))
            val cmd = byteArrayOf(CMD_PRN.toByte(), valuePRN[1], valuePRN[0])
            if (writeControlPointData(mDevice, cmd)) {
                mResponseChannel.receive() as Boolean?
            } else {
                false
            }
        } ?: false
    }

    private suspend fun listenPRNResult(): Boolean {
        val result = mPRNChannel.receive()
        val computeCrc = computeCrc(dfuPackage, 0, result.offset)
        if (computeCrc == result.crc) {
            return true
        }
        return false
    }

    protected fun computeCrc(byteArray: ByteArray, start: Int = 0, end: Int = byteArray.size): Long {
        mCrc.reset()
        mCrc.update(byteArray.copyOfRange(start, end))
        return mCrc.value
    }

    protected suspend fun computeSelectResult(selectCmdResult: SelectCmdResult): Int {
        return withContext(Dispatchers.Default) {
            if (selectCmdResult.offset > 0 && selectCmdResult.offset <= dfuPackage.size) {
                val crcValue = computeCrc(dfuPackage.copyOfRange(0, selectCmdResult.offset))
                when {
                    crcValue != selectCmdResult.crc -> {
                        Log.i("MCJ","重头开始传输")
                        COMPUTE_RESULT_START
                    }
                    selectCmdResult.offset == dfuPackage.size -> {
                        Log.i("MCJ","已传输完成")
                        COMPUTE_RESULT_COMPLETE
                    }
                    else -> {
                        Log.i("MCJ","接着上一次开始传输")
                        COMPUTE_RESULT_CONTINUE
                    }
                }
            } else {
                Log.i("MCJ","重头开始传输")
                COMPUTE_RESULT_START
            }
        }
    }

    protected suspend fun sendCreate(pagePackage: ByteArray): Boolean {
        return withTimeoutOrNull(5000) {
            val length = HexUtil.hexStringToBytes(String.format("%08x", pagePackage.size))
            val cmd = byteArrayOf(CMD_CREATE.toByte(), getType().toByte(), length[3], length[2], length[1], length[0])
            writeControlPointData(mDevice, cmd)
            mResponseChannel.receive() as Boolean?
        } ?: false
    }

    protected suspend fun sendPackage(pagePackage: ByteArray): Boolean {
        var failTimes = 0
        var offset = 0
        var end = 0
        var writeTime = 0
        Log.i("MCJ","发送数据中")
        while (true) {
            val writePackageDataResult = withTimeoutOrNull(500) {
                end = offset + STEP
                if (end > pagePackage.size) end = pagePackage.size
                writeBluetooth(mDevice, pagePackage.copyOfRange(offset, end))
            }
            if (writePackageDataResult == true) {
                val numberPRN = getPRNNumber()
                if (numberPRN > 0 && end < pagePackage.size) {
                    writeTime++
                    if (writeTime == numberPRN) {
                        writeTime = 0
                        if (!listenPRNResult().negativeLog("监听 PRN 失败")) {
                            return false
                        }
                    }
                }
                if (end >= pagePackage.size) {
                    break
                } else {
                    offset += STEP
                }
            } else {
                failTimes += 1
                if (failTimes > 10) {
                    break
                }
            }
        }
        return end == pagePackage.size
    }

    protected suspend fun sendCheckSum(): CheckSumResult? {
        val cmd = byteArrayOf(CMD_CALCULATE_CHECKSUM.toByte())
        if (writeControlPointData(mDevice, cmd)) {
            return mChecksumChannel.receive().negativeLog("接收 checkSum 结果失败")
        }
        return null
    }

    protected suspend fun sendExecute(): Boolean {
        val cmd = byteArrayOf(CMD_EXECUTE.toByte())
        return if (writeControlPointData(mDevice, cmd)) {
            val value = mResponseChannel.receive() ?: false
            value as Boolean
        } else {
            false
        }
    }

    protected fun computeCheckSum(checkSumResult: CheckSumResult?): Boolean {
        if (checkSumResult == null) {
            return false
        }
        val offset = checkSumResult.offset
        val crc = checkSumResult.crc
        val computeCrc = computeCrc(dfuPackage.copyOfRange(0, offset))
        if (crc == computeCrc) {
            return true
        }
        return false
    }

    private suspend fun writePackage(selectResult: SelectCmdResult, force: Boolean): Boolean {
        val pageSize = ceil(dfuPackage.size.toDouble() / selectResult.maxSize).toInt().log("pageSize: ")
        var startOffset = 0
        var end = 0
        if (!force && computeSelectResult(selectResult) == COMPUTE_RESULT_COMPLETE) {
            return true
        }
        var currentPage = selectResult.offset / selectResult.maxSize
        for (page in currentPage until pageSize) {
            startOffset = page * selectResult.maxSize
            end = startOffset + selectResult.maxSize
            if (end > dfuPackage.size) end = dfuPackage.size
            val pagePackage = dfuPackage.copyOfRange(startOffset, end)
            if (!cmdRetry {
                        sendCreate(pagePackage).log("发送第 $page 页 create 指令结果:  ")
                                && sendPackage(pagePackage).log("发送第 $page 页数据结果: ")
                    }) {
                return false
            }
            if (!computeCheckSum(sendCheckSum()).log("计算第 $page 页结果: ")) {
                return false
            }
            if (!cmdRetry { sendExecute().log("执行第 $page 页结果: ") }) {
                return false
            }
        }
        return true
    }

    private suspend fun cmdRetry(retryMax: Int = 3, cmdFun: suspend () -> Boolean): Boolean {
        var retryTimes = 0
        var success = false
        while (!success && retryTimes < retryMax) {
            success = cmdFun()
            retryTimes++
        }
        return success
    }

    protected fun onDfuCharacteristicChanged(data: ByteArray?) {
        val channel = mResponseChannel
        if (data != null) {
            val header = data[0]
            val cmd = data[1]
            val result = data[2]
            if (header == CMD_RESPONSE_HEADER.toByte()
                    && result == RESULT_SUCCESS.toByte()) {
                if (cmd == CMD_SELECT.toByte()) {
                    val maxSize = HexUtil.formatHexString(byteArrayOf(data[6], data[5], data[4], data[3])).toInt(16)
                    val offset = HexUtil.formatHexString(byteArrayOf(data[10], data[9], data[8], data[7])).toInt(16)
                    val crc = HexUtil.formatHexString(byteArrayOf(data[14], data[13], data[12], data[11])).toLong(16)
                    coroutineScope.launch {
                        channel.send(SelectCmdResult(maxSize, offset, crc))
                    }
                    return
                }
                if (cmd == CMD_PRN.toByte()) {
                    coroutineScope.launch {
                        channel.send(true)
                    }
                    return
                }
                if (cmd == CMD_CREATE.toByte()) {
                    coroutineScope.launch {
                        channel.send(true)
                    }
                    return
                }
                if (cmd == CMD_CALCULATE_CHECKSUM.toByte()) {
                    coroutineScope.launch {
                        val offset = HexUtil.formatHexString(byteArrayOf(data[6], data[5], data[4], data[3])).toInt(16)
                        val crc = HexUtil.formatHexString(byteArrayOf(data[10], data[9], data[8], data[7])).toLong(16)
                        if (offset == dfuPackage.size || offset % mSelectCmdResult.maxSize == 0) {
                            mChecksumChannel.send(CheckSumResult(offset, crc))
                        } else {
                            mPRNChannel.send(CheckSumResult(offset, crc))
                        }
                    }
                    return
                }
                if (cmd == CMD_EXECUTE.toByte()) {
                    coroutineScope.launch {
                        channel.send(true)
                    }
                    return
                }
                coroutineScope.launch {
                    mResponseChannel.send(null)
                }
            }
        } else {
            coroutineScope.launch {
                mResponseChannel.send(null)
            }
        }
    }
}