package com.sumian.device.util

import android.os.*
import android.util.Log
import com.clj.fastble.utils.HexUtil
import com.sumian.device.callback.BleCommunicationWatcher
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.data.DeviceConnectStatus
import com.sumian.device.manager.DeviceManager
import java.util.concurrent.PriorityBlockingQueue

object CmdQueue {
    private var mCommunicationWatcher: BleCommunicationWatcher? = null
    private var mPutCmdHandler: Handler? = null
    private var mPutCmdThread: HandlerThread? = null
    @Volatile
    private var mSyncInfoQueue: PriorityBlockingQueue<Cmd> = PriorityBlockingQueue()
    private var mSyncInfoHandler: Handler? = null
    private var mSyncInfoThread: HandlerThread? = null
    private var mCurrentSyncInfoCmd: Cmd? = null
    private var mCurrentRetrySyncInfoCmd: Cmd? = null
    @Volatile
    private var mIsBlockSyncInfoQueue = false

    const val MSG_SEND = 1
    const val MSG_TIMEOUT = 2
    const val MSG_ON_READ = 3
    const val MSG_ON_WRITE = 4

    const val QUEUE_MESSAGE_INTERVAL = 100L

    const val ERROR_CODE_TIMEOUT = 1
    const val ERROR_CODE_TIMEOUT_AND_RETRY_FAIL = 2

    private const val CMD_RETRY_TIME = 3
    private const val TIME_SYNC_INFO_TIMEOUT = 3000L
    const val EXTRA_CMD_HEX_STRING = "extra_cmd_hex_string"
    const val EXTRA_CMD_HEX_ARRAY = "extra_cmd_hex_array"
    const val EXTRA_CMD_WRITE_RESULT = "extra_cmd_write_result"

    fun registerDeviceStatusListener() {
        Log.i("MCJ", "cmdQueue register")
        DeviceManager.registerDeviceStatusListener(object : DeviceStatusListener {
            override fun onStatusChange(type: String, data: Any?) {
                Log.i("MCJ", "onStatusChange: type: $type data:$data")
                if (type == DeviceManager.EVENT_MONITOR_CONNECT_STATUS_CHANGE) {
                    blockSyncInfo(false)
                    if (data != null) {
                        var state = data as DeviceConnectStatus
                        if (state == DeviceConnectStatus.CONNECTED) {
                            startSyncInfoQueue()
                        } else if (state == DeviceConnectStatus.DISCONNECTED) {
                            stopSyncInfoQueue()
                        }
                    }
                }
            }
        })
    }

    private fun stopSyncInfoQueue() {
        mSyncInfoThread?.quit()
        mSyncInfoThread = null
        mSyncInfoHandler = null
        var watcher = mCommunicationWatcher
        if (watcher != null) DeviceManager.unregisterBleCommunicationWatcher(watcher)
        mPutCmdThread?.quit()
        mPutCmdThread = null
        mPutCmdHandler = null
        mSyncInfoQueue.clear()
    }

    private fun startSyncInfoQueue() {
        Log.i("MCJ", "startSyncInfoQueue")
        mSyncInfoQueue.clear()
        mSyncInfoThread = HandlerThread("SyncInfo")
        mSyncInfoThread!!.start()
        mSyncInfoHandler = object : Handler(mSyncInfoThread!!.looper) {
            override fun handleMessage(msg: Message?) {
                when (msg?.what) {
                    MSG_SEND -> {
                        var cmd = takeSyncInfoCmd()
                        Log.i("MCJ", "Sync: ${HexUtil.formatHexString(cmd.cmd)}")
                        mCurrentSyncInfoCmd = cmd
                        mCurrentRetrySyncInfoCmd = cmd.copy(resultCmds = mutableListOf<String>().apply {
                            addAll(cmd.resultCmds)
                        })
                        sendMsgSyncInfoTimeOut()
                        DeviceManager.writeData(cmd.cmd)
                    }
                    MSG_TIMEOUT -> {
                        Log.i("MCJ", "TIMEOUT: ${HexUtil.formatHexString(mCurrentSyncInfoCmd?.cmd)}")
                        var currentCmd = mCurrentSyncInfoCmd
                        if (currentCmd == null) {
                            sendMsgSyncInfo()
                            return
                        }
                        mCurrentSyncInfoCmd = null
                        var retryCmd = mCurrentRetrySyncInfoCmd
                        if (retryCmd == null) {
                            sendMsgSyncInfo()
                            return
                        }
                        retryCmd(retryCmd)
                    }
                    MSG_ON_WRITE -> {
                        var currentCmd: Cmd? = mCurrentSyncInfoCmd ?: return
                        var currentCmdHeader = HexUtil.formatHexString(currentCmd!!.cmd).substring(2, 4)
                        var bundle = msg.obj as Bundle
                        var cmd = bundle.getByteArray(EXTRA_CMD_HEX_ARRAY)
                        var cmdStr = bundle.getString(EXTRA_CMD_HEX_STRING)
                        var cmdHeader = cmdStr.substring(2, 4)
                        var success = bundle.getBoolean(EXTRA_CMD_WRITE_RESULT)
                        if (cmdHeader == currentCmdHeader) {
                            Log.i("MCJ", "WRITE: ${HexUtil.formatHexString(mCurrentSyncInfoCmd?.cmd)} success: $success")
                            if (success) {
                                if (currentCmd!!.resultCmds.isEmpty()) {
                                    mCurrentSyncInfoCmd = null
                                    sendMsgSyncInfo()
                                } else {
                                    sendMsgSyncInfoTimeOut()
                                }
                            } else {
                                var retryCmd = mCurrentRetrySyncInfoCmd ?: return
                                retryCmd(retryCmd)
                            }
                        }
                    }
                    MSG_ON_READ -> {
                        var currentCmd: Cmd? = mCurrentSyncInfoCmd ?: return
                        var currentCmdHeader = HexUtil.formatHexString(currentCmd!!.cmd).substring(2, 4)
                        var bundle = msg.obj as Bundle
                        var cmd = bundle.getByteArray(EXTRA_CMD_HEX_ARRAY)
                        var cmdStr = bundle.getString(EXTRA_CMD_HEX_STRING)
                        var cmdHeader = cmdStr?.substring(2, 4)
                        if (cmdHeader == currentCmdHeader) {
                            Log.i("MCJ", "READ: $cmdStr")
                            var resultCmds = currentCmd.resultCmds
                            if (resultCmds.isEmpty()) {
                                mCurrentSyncInfoCmd = null
                                currentCmd.callback?.onResponse(cmd, cmdStr)
                                sendMsgSyncInfo()
                            } else {
                                sendMsgSyncInfoTimeOut()
                                for (resultCmd in resultCmds) {
                                    Log.i("MCJ", "resultCmd: $resultCmd}")
                                }
                                if (resultCmds[0].substring(2, 4) == cmdHeader) {
                                    resultCmds.removeAt(0)
                                }
                                Log.i("MCJ", "currentCmd.resultCmds.size: ${currentCmd.resultCmds.size}")
                                if (resultCmds.isEmpty()) {
                                    mCurrentSyncInfoCmd = null
                                    currentCmd.callback?.onResponse(cmd, cmdStr)
                                    sendMsgSyncInfo()
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.i("MCJ", "start sync info")
        var communicationWatcher = object : BleCommunicationWatcher {
            override fun onRead(data: ByteArray, hexString: String) {
                mSyncInfoHandler?.sendMessage(Message.obtain().apply {
                    what = MSG_ON_READ
                    obj = Bundle().apply {
                        putByteArray(EXTRA_CMD_HEX_ARRAY, data)
                        putString(EXTRA_CMD_HEX_STRING, hexString)
                    }
                })
            }

            override fun onWrite(data: ByteArray, hexString: String, success: Boolean, errorMsg: String?) {
                mSyncInfoHandler?.sendMessage(Message.obtain().apply {
                    what = MSG_ON_WRITE
                    obj = Bundle().apply {
                        putByteArray(EXTRA_CMD_HEX_ARRAY, data)
                        putString(EXTRA_CMD_HEX_STRING, hexString)
                        putBoolean(EXTRA_CMD_WRITE_RESULT, success)
                    }
                })
            }
        }
        DeviceManager.registerBleCommunicationWatcher(communicationWatcher)
        mCommunicationWatcher = communicationWatcher
        sendMsgSyncInfo()
        mPutCmdThread = HandlerThread("putSyncInfo")
        mPutCmdThread!!.start()
        mPutCmdHandler = Handler(mPutCmdThread!!.looper)
    }

    private fun retryCmd(cmd: Cmd) {
        if (!cmd.retry || cmd.retryTime >= CMD_RETRY_TIME) {
            cmd.callback?.onFail(if (!cmd.retry) ERROR_CODE_TIMEOUT else ERROR_CODE_TIMEOUT_AND_RETRY_FAIL, "${cmd.cmd} timeout")
            sendMsgSyncInfo()
            return
        }
        Log.i("MCJ", "retryCmd ${HexUtil.formatHexString(cmd.cmd)}")
        cmd.retryTime += 1
        cmd.priority = Cmd.Priority.RETRY
        mSyncInfoQueue.put(cmd)
        sendMsgSyncInfo()
    }

    private fun sendMsgSyncInfo() {
        removeMsgSyncInfoTimeOut()
        mSyncInfoHandler?.removeMessages(MSG_SEND)
        mSyncInfoHandler?.sendEmptyMessageDelayed(MSG_SEND, QUEUE_MESSAGE_INTERVAL)
    }

    private fun sendMsgSyncInfoTimeOut() {
        removeMsgSyncInfoTimeOut()
        mSyncInfoHandler?.sendEmptyMessageDelayed(MSG_TIMEOUT, TIME_SYNC_INFO_TIMEOUT)
    }

    private fun removeMsgSyncInfoTimeOut() {
        mSyncInfoHandler?.removeMessages(MSG_TIMEOUT)
    }

    fun putSyncInfoCmd(cmd: Cmd) {
        mPutCmdHandler?.post {
            SystemClock.sleep(10)
            cmd.timeMill = System.currentTimeMillis()
            mSyncInfoQueue.put(cmd)
        }
    }

    fun takeSyncInfoCmd(): Cmd {
        while (mIsBlockSyncInfoQueue) {
        }
        return mSyncInfoQueue.take()
    }

    fun blockSyncInfo(block: Boolean) {
        Log.i("MCJ","blockSyncInfo: $block")
        mIsBlockSyncInfoQueue = block
    }

    fun isBlockSyncInfo(): Boolean {
        return mIsBlockSyncInfoQueue
    }
}